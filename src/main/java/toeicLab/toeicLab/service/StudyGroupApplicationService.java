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
import toeicLab.toeicLab.user.StudyGroupApplicationForm;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupApplicationService {
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;
    private final StudyGroupRepository studyGroupRepository;

    /**
     * 사용자들이 제출한 스터디그룹 신청서를 조회한다.
     * @return false
     */
    public List<StudyGroupApplication> getStudyGroupApplicationList() {
        return studyGroupApplicationRepository.findAllByMatching(false);
    }

    /**
     * 서로 조건이 충족하는 4개의 신청서가 모이면 스터디 그룹을 형성한다.
     * @return result
     */
    public List<StudyGroup> matchStudyGroups() {
        List<StudyGroupApplication> applicationPool = getStudyGroupApplicationList();
        List<StudyGroup> result = new ArrayList<>();
        while (applicationPool.size() > 3) {
            StudyGroupApplication target = applicationPool.get(0);
            applicationPool.remove(0);
            StudyGroup studyGroup = matchOneStudyGroup(target, applicationPool);
            if (studyGroup.getMembers().size() == 4) {
                Member readerMember = studyGroup.getMembers().get(0);
                studyGroup.setReaderId(readerMember.getId());
                studyGroup.setName(readerMember.getId().toString());
                result.add(studyGroup);
                studyGroupRepository.save(studyGroup);
                for (Member member : studyGroup.getMembers()) {
                    applicationPool.remove(member.getStudyGroupApplication());

                }
            }
        }
        return result;
    }

    /**
     * 사용자가 제출한 신청서의 체크 된 항목들을 조회한다.
     * @param target
     * @param applicationPool
     * @return result
     */
    public StudyGroup matchOneStudyGroup(StudyGroupApplication target, List<StudyGroupApplication> applicationPool) {
        List<StudyGroupApplication> list1 = findAgeMatches(target, applicationPool);
        List<StudyGroupApplication> list2 = findLevelMatches(target, list1);
        List<StudyGroupApplication> list3 = findDayMatches(target, list2);
        List<StudyGroupApplication> list4 = findGenderMatches(target, list3);

        StudyGroup result = new StudyGroup();
        if (list4.size() >= 4) {
            List<Member> members = new ArrayList<>();
            members.add(list4.get(0).getMember());
            members.add(list4.get(1).getMember());
            members.add(list4.get(2).getMember());
            members.add(list4.get(3).getMember());
            result.setMembers(members);
        }
        return result;
    }

    /**
     * 사용자가 제출한 신청서의 날짜 value 환산한다.
     * @param a
     * @param b
     * @return result
     */
    public int gcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    /**
     * 사용자가 제출한 신청서의 날짜를 조회한다.
     * @param application
     * @param list2
     * @return result
     */
    private List<StudyGroupApplication> findDayMatches(StudyGroupApplication application, List<StudyGroupApplication> list2) {
        List<StudyGroupApplication> result = new ArrayList<>();
        list2.remove(0);
        result.add(application);

        for (int i = 0; i < list2.size(); i++) {
            int count = 0;
            for (int j = 0; j < result.size(); j++) {

                if (gcd((int) list2.get(i).getValue(), (int) result.get(j).getValue()) != 1) {
                   ++count;
                }
            }
            if (count == result.size()) {
                result.add(list2.get(i));
            }

        }
        return result;
    }

    /**
     * 사용자가 제출한 신청서의 성별을 조회한다.
     * @param application
     * @param list3
     * @return result
     */
    private List<StudyGroupApplication> findGenderMatches(StudyGroupApplication application, List<StudyGroupApplication> list3) {
        List<StudyGroupApplication> result = new ArrayList<>();
        list3.remove(0);
        result.add(application);

        for (int i = 0; i < list3.size(); i++) {
            if (application.getValue() % list3.get(i).getMember().getGenderType().get() == 0) {
                int count = 0;
                for (int j = 0; j < result.size(); j++) {

                    if (list3.get(i).getValue() % result.get(j).getMember().getGenderType().get() == 0) {
                        count++;

                    }
                }
                if (count == result.size()) {
                    result.add(list3.get(i));
                }
            }
        }
        return result;
    }

    /**
     * 신청서에 체크 된 나이에 값을 부여한다.
     * @param application
     * @return tagValue
     */
    private int getTagValueFromAge(StudyGroupApplication application) {
        int tagValue = StudyGroupApplicationTag.AGE_10S.get()
                * StudyGroupApplicationTag.AGE_20S.get()
                * StudyGroupApplicationTag.AGE_30S.get();

        switch (application.getMember().getAge() / 10) {
            case 1:
                tagValue = StudyGroupApplicationTag.AGE_10S.get();
                break;
            case 2:
                tagValue = StudyGroupApplicationTag.AGE_20S.get();
                break;
            case 3:
                tagValue = StudyGroupApplicationTag.AGE_30S.get();
                break;
            default:
                break;
        }

        return tagValue;
    }

    /**
     * 사용자가 제출한 신청서의 나이를 조회한다.
     * @param target
     * @param applicationPool
     * @return result
     */
    private List<StudyGroupApplication> findAgeMatches(StudyGroupApplication target, List<StudyGroupApplication> applicationPool) {
        List<StudyGroupApplication> result = new ArrayList<>();
        result.add(target);

        int[] ageCheckList = new int[]{StudyGroupApplicationTag.AGE_10S.get(),
                StudyGroupApplicationTag.AGE_20S.get(),
                StudyGroupApplicationTag.AGE_30S.get()};

        for (int checkAge : ageCheckList) {
            if (target.getValue() % checkAge == 0) { // 타켓이 체크리스트의 태그를 눌렀다면
                for (int i = 0; i < applicationPool.size(); i++) {
                    if (getTagValueFromAge(applicationPool.get(i)) == checkAge) {
                        int count = 0;
                        for (int j = 0; j < result.size(); j++) {

                            if (applicationPool.get(i).getValue() % getTagValueFromAge(result.get(j)) == 0) {
                                ++count;
                            }
                        }
                        if (count == result.size()) {
                            result.add(applicationPool.get(i));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 사용자가 제출한 신청서의 레벨을 조회한다.
     * @param application
     * @param list1
     * @return result
     */
    private List<StudyGroupApplication> findLevelMatches(StudyGroupApplication application, List<StudyGroupApplication> list1) {
        List<StudyGroupApplication> result = new ArrayList<>();
        list1.remove(0);
        result.add(application);

        int[] LevelCheckList = new int[]{StudyGroupApplicationTag.LEVEL_BEGINNER.get(),
                StudyGroupApplicationTag.LEVEL_INTERMEDIATE.get(),
                StudyGroupApplicationTag.LEVEL_ADVANCED.get()};

        for (int checkLevel : LevelCheckList) {
            if (application.getValue() % checkLevel == 0) {
                for (int i = 0; i < list1.size(); i++) {
                    if (checkLevel == list1.get(i).getMember().getLevelType().get()) {
                        int count = 0;
                        for (int j = 0; j < result.size(); j++) {
                            if (result.get(j).getMember().getLevelType().get() == list1.get(i).getMember().getLevelType().get()) {
                                count++;
                            }
                        }
                        if (count == result.size()) {
                            result.add(list1.get(i));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 조건이 맞는 새로운 스터디 그룹을 형성한다.
     * @param studyGroupApplicationForm
     * @param member
     */
    public void createNewStudyGroupApplication(StudyGroupApplicationForm studyGroupApplicationForm, Member member) {
        int value=1;
        for (StudyGroupApplicationTag t : studyGroupApplicationForm.getTags()){
            value *= t.get();
        }

        StudyGroupApplication studyGroupApplication = StudyGroupApplication.builder()
                .member(member)
                .value(value)
                .matching(false)
                .submitTime(LocalDateTime.now())
                .tags(Arrays.asList(studyGroupApplicationForm.getTags().clone()))
                .build();

        if (member.getStudyGroupApplication() == null) {
            studyGroupApplicationRepository.save(studyGroupApplication);
        } else {
            member.setStudyGroupApplication(studyGroupApplication);
        }
    }
}
