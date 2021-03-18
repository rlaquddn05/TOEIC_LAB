package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.StudyGroup;
import toeicLab.toeicLab.domain.StudyGroupApplication;
import toeicLab.toeicLab.domain.StudyGroupApplicationTag;
import toeicLab.toeicLab.repository.StudyGroupApplicationRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupApplicationService {
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;

    public List<StudyGroupApplication> getStudyGroupApplicationList() {
        return studyGroupApplicationRepository.findAll();
    }

    public List<StudyGroup> matchStudyGroups(){
        List<StudyGroupApplication> applicationPool = getStudyGroupApplicationList();
        List<StudyGroup> result = new ArrayList<>();
        while (applicationPool.size()>3){
            StudyGroup studyGroup = matchOneStudyGroup(applicationPool.get(0), getStudyGroupApplicationList());
            if (studyGroup.getMembers().size()!=0){
                result.add(studyGroup);
                for(Member member : studyGroup.getMembers()){
                    applicationPool.remove(member.getStudyGroupApplication());
                }
            } else {
                applicationPool.remove(applicationPool.get(0));
            }
        }
        return result;
    }

    public StudyGroup matchOneStudyGroup(StudyGroupApplication application, List<StudyGroupApplication> applicationPool) {
        List<StudyGroupApplication> list1 = findAgeMatches(application, applicationPool);
        List<StudyGroupApplication> list2 = findLevelMatches(application, list1);
        List<StudyGroupApplication> list3 = findDayMatches(application, list2);
        List<StudyGroupApplication> list4 = findGenderMatches(application, list3);

        if (list4.size() >= 3) {
            application.setMatching(true);
        }

        StudyGroup studyGroup = new StudyGroup();
        List<Member> members = new ArrayList<>();
        members.add(list4.get(0).getMember());
        members.add(list4.get(1).getMember());
        members.add(list4.get(2).getMember());
        studyGroup.setMembers(members);
        return studyGroup;
    }

    private int gcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    private List<StudyGroupApplication> findDayMatches(StudyGroupApplication application, List<StudyGroupApplication> list2) {
        List<StudyGroupApplication> result = new ArrayList<>();
        for (StudyGroupApplication s : list2) {
            if (gcd(s.getValue(), application.getValue()) != 1) {
                result.add(s);
            }
        }
        return result;
    }

    private List<StudyGroupApplication> findGenderMatches(StudyGroupApplication application, List<StudyGroupApplication> list3) {
        List<StudyGroupApplication> result = new ArrayList<>();

        for (StudyGroupApplication s : list3) {
            if (application.getValue() % s.getMember().getGenderType().get() == 0
                    && s.getValue() % application.getMember().getGenderType().get() == 0) {

                result.add(s);
            }
        }
        return result;
    }

    private List<StudyGroupApplication> findAgeMatches(StudyGroupApplication application, List<StudyGroupApplication> applicationPool) {
        int tagValueOfThisPersonsAge = StudyGroupApplicationTag.AGE_10S.get()
                * StudyGroupApplicationTag.AGE_20S.get()
                * StudyGroupApplicationTag.AGE_30S.get();

        switch (application.getMember().getAge() / 10) {
            case 1:
                tagValueOfThisPersonsAge = StudyGroupApplicationTag.AGE_10S.get();
            case 2:
                tagValueOfThisPersonsAge = StudyGroupApplicationTag.AGE_20S.get();
            case 3:
                tagValueOfThisPersonsAge = StudyGroupApplicationTag.AGE_30S.get();
        }
        List<StudyGroupApplication> result = new ArrayList<>();

        int[] ageCheckList = new int[]{StudyGroupApplicationTag.AGE_10S.get(),
                StudyGroupApplicationTag.AGE_20S.get(),
                StudyGroupApplicationTag.AGE_30S.get()};

        for (int checkAge : ageCheckList) {
            if (application.getValue() % checkAge == 0) {
                for (StudyGroupApplication s : applicationPool) {
                    int sAge = s.getMember().getAge();
                    if ((checkAge == 2 && sAge >= 10 && sAge < 20)
                            || (checkAge == 3 && sAge >= 20 && sAge < 30)
                            || (checkAge == 5 && sAge >= 30 && sAge < 40)) {
                        if (s.getValue() % tagValueOfThisPersonsAge == 0) {
                            result.add(s);
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<StudyGroupApplication> findLevelMatches(StudyGroupApplication application, List<StudyGroupApplication> list1) {
        int tagValueOfThisPersonsLevel = application.getMember().getLevelType().get();
        List<StudyGroupApplication> result = new ArrayList<>();

        int[] LevelCheckList = new int[]{StudyGroupApplicationTag.LEVEL_BEGINNER.get(),
                StudyGroupApplicationTag.LEVEL_INTERMEDIATE.get(),
                StudyGroupApplicationTag.LEVEL_ADVANCED.get()};

        for (int checkLevel : LevelCheckList) {
            if (application.getValue() % checkLevel == 0) {
                for (StudyGroupApplication s : list1) {
                    int sLevel = s.getMember().getLevelType().get();
                    if (checkLevel == sLevel && s.getValue() % tagValueOfThisPersonsLevel == 0) {
                        result.add(s);
                    }
                }
            }
        }
        return result;
    }
}
