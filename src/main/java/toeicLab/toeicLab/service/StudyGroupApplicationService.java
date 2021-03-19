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

    public List<StudyGroup> matchStudyGroups() {
        List<StudyGroupApplication> applicationPool = getStudyGroupApplicationList();
        List<StudyGroup> result = new ArrayList<>();
        while (applicationPool.size() > 3) {
            StudyGroup studyGroup = matchOneStudyGroup(applicationPool.get(0), getStudyGroupApplicationList());
            if (studyGroup.getMembers().size() != 0) {
                result.add(studyGroup);
                for (Member member : studyGroup.getMembers()) {
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
        result.add(application);

        for (StudyGroupApplication s1 : list2) {
            for (StudyGroupApplication s2 : result) {
                int count = 1;
                if (gcd(s1.getValue(), s2.getValue()) != 1) {
                    if (count == result.size()) {
                        result.add(s1);
                    }
                }
            }

        }
        return result;
    }

    private List<StudyGroupApplication> findGenderMatches(StudyGroupApplication application, List<StudyGroupApplication> list3) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        for (StudyGroupApplication s1 : list3) {
            if (application.getValue() % s1.getMember().getGenderType().get() == 0) {
                for (StudyGroupApplication s2 : result) {
                    int count = 1;
                    if (s1.getValue() % s2.getMember().getGenderType().get() == 0) {
                        count++;
                        if (count == result.size()) {
                            result.add(s1);
                        }
                    }
                }
                result.add(s1);
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
        }

        return tagValue;
    }

    private List<StudyGroupApplication> findAgeMatches(StudyGroupApplication application, List<StudyGroupApplication> applicationPool) {
        int tagValueOfThisPersonsAge = getTagValueFromAge(application);
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(application);

        int[] ageCheckList = new int[]{StudyGroupApplicationTag.AGE_10S.get(),
                StudyGroupApplicationTag.AGE_20S.get(),
                StudyGroupApplicationTag.AGE_30S.get()};

        for (int checkAge : ageCheckList) {
            if (application.getValue() % checkAge == 0) {
                for (StudyGroupApplication s1 : applicationPool) {
                    if (checkAge == getTagValueFromAge(s1)) {
                        for (StudyGroupApplication s2 : result) {
                            int count = 1;
                            if (getTagValueFromAge(s2) == getTagValueFromAge(s1)) {
                                count++;
                                if (count == result.size()) {
                                    result.add(s1);
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
                for (StudyGroupApplication s1 : list1) {
                    int sLevel = s1.getMember().getLevelType().get();
                    if (checkLevel == sLevel) {
                        for (StudyGroupApplication s2 : result) {
                            int count = 1;
                            if (s2.getMember().getLevelType().get() == s1.getMember().getLevelType().get()) {
                                count++;
                                if (count == result.size()) {
                                    result.add(s1);
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
