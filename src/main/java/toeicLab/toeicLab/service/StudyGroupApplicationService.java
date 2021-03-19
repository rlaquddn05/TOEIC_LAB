package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.StudyGroup;
import toeicLab.toeicLab.domain.StudyGroupApplication;
import toeicLab.toeicLab.domain.StudyGroupApplicationTag;
import toeicLab.toeicLab.repository.StudyGroupApplicationRepository;
import toeicLab.toeicLab.repository.StudyGroupRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupApplicationService {
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;
    private final StudyGroupRepository studyGroupRepository;


    public List<StudyGroupApplication> getStudyGroupApplicationList() {
        return studyGroupApplicationRepository.findAll();
    }

    public List<StudyGroup> matchStudyGroups() {
        List<StudyGroupApplication> applicationPool = getStudyGroupApplicationList();
        List<StudyGroup> result = new ArrayList<>();
        while (applicationPool.size() > 3) {
            StudyGroupApplication target = applicationPool.get(0);
            applicationPool.remove(0);
            StudyGroup studyGroup = matchOneStudyGroup(target, applicationPool);
            if (studyGroup.getMembers() != null) {
                result.add(studyGroup);
                studyGroupRepository.save(studyGroup);
                for (Member member : studyGroup.getMembers()) {
                    applicationPool.remove(member.getStudyGroupApplication());
                }
            }
        }
        return result;
    }

    public StudyGroup matchOneStudyGroup(StudyGroupApplication application, List<StudyGroupApplication> applicationPool) {
        List<StudyGroupApplication> list1 = findAgeMatches(application, applicationPool);
        List<StudyGroupApplication> list2 = findLevelMatches(application, list1);
        List<StudyGroupApplication> list3 = findDayMatches(application, list2);
        List<StudyGroupApplication> list4 = findGenderMatches(application, list3);

        StudyGroup result = new StudyGroup();
        if (list4.size() >= 3) {
            List<Member> members = new ArrayList<>();
            members.add(list4.get(0).getMember());
            members.add(list4.get(1).getMember());
            members.add(list4.get(2).getMember());
            members.add(list4.get(3).getMember());
            result.setMembers(members);
        }
        return result;
    }

    private int gcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    private List<StudyGroupApplication> findDayMatches(StudyGroupApplication application, List<StudyGroupApplication> list2) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        for (int i = 0; i<list2.size(); i++) {
            for (int j = 0; j<result.size(); j++) {
                int count = 1;
                if (gcd(list2.get(i).getValue(), result.get(j).getValue()) != 1) {
                    if (count == result.size()) {
                        result.add(list2.get(i));
                    }
                }
            }

        }
        return result;
    }

    private List<StudyGroupApplication> findGenderMatches(StudyGroupApplication application, List<StudyGroupApplication> list3) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        for (int i = 0; i<list3.size(); i++) {
            if (application.getValue() % list3.get(i).getMember().getGenderType().get() == 0) {
                for (int j = 0; j<result.size(); j++) {
                    int count = 1;
                    if (list3.get(i).getValue() % result.get(j).getMember().getGenderType().get() == 0) {
                        count++;
                        if (count == result.size()) {
                            result.add(list3.get(i));
                        }
                    }
                }
                result.add(list3.get(i));
            }
        }
        return result;
    }

    private int getTagValueFromAge(StudyGroupApplication application) {
        int tagValue = StudyGroupApplicationTag.AGE_10S.get()
                * StudyGroupApplicationTag.AGE_20S.get()
                * StudyGroupApplicationTag.AGE_30S.get();

        switch (application.getMember().getAge() / 10) {
            case 1:
                tagValue = StudyGroupApplicationTag.AGE_10S.get();
            case 2:
                tagValue = StudyGroupApplicationTag.AGE_20S.get();
            case 3:
                tagValue = StudyGroupApplicationTag.AGE_30S.get();
            default: break;
        }

        return tagValue;
    }

    private List<StudyGroupApplication> findAgeMatches(StudyGroupApplication application, List<StudyGroupApplication> applicationPool) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        int[] ageCheckList = new int[]{StudyGroupApplicationTag.AGE_10S.get(),
                StudyGroupApplicationTag.AGE_20S.get(),
                StudyGroupApplicationTag.AGE_30S.get()};

        for (int checkAge : ageCheckList) {
            if (application.getValue() % checkAge == 0) {
                for (int i = 0; i<applicationPool.size(); i++) {
                    if (checkAge == getTagValueFromAge(applicationPool.get(i))) {
                        for (int j = 0; j<result.size(); j++) {
                            int count = 1;
                            if (getTagValueFromAge(result.get(j)) == getTagValueFromAge(applicationPool.get(i))) {
                                count++;
                                if (count == result.size()) {
                                    result.add(applicationPool.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<StudyGroupApplication> findLevelMatches(StudyGroupApplication application, List<StudyGroupApplication> list1) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        int[] LevelCheckList = new int[]{StudyGroupApplicationTag.LEVEL_BEGINNER.get(),
                StudyGroupApplicationTag.LEVEL_INTERMEDIATE.get(),
                StudyGroupApplicationTag.LEVEL_ADVANCED.get()};

        for (int checkLevel : LevelCheckList) {
            if (application.getValue() % checkLevel == 0) {
                for (int i = 0; i<list1.size(); i++) {
                    int sLevel = list1.get(i).getMember().getLevelType().get();
                    if (checkLevel == sLevel) {
                        for (int j = 0; j<result.size(); j++) {
                            int count = 1;
                            if (result.get(j).getMember().getLevelType().get() == list1.get(i).getMember().getLevelType().get()) {
                                count++;
                                if (count == result.size()) {
                                    result.add(list1.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
