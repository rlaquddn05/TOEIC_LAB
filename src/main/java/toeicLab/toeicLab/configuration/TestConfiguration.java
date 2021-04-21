package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.repository.StudyGroupApplicationRepository;
import toeicLab.toeicLab.service.StudyGroupApplicationService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Profile("test")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final StudyGroupApplicationService studyGroupApplicationService;
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;
    private static final int NUMBER_OF_DUMMY_USERS = 150;
    private static final LevelType[] levelTypes = {LevelType.BEGINNER,
            LevelType.INTERMEDIATE, LevelType.ADVANCED};
    private static final GenderType[] genderTypes = {GenderType.MALE, GenderType.FEMALE};
    private static final int[] tagValues = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    private static final int PART1_NUMBER = 100;
    private static final int PART2_NUMBER = 100;
    private static final int PART3_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART4_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART6_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART7_SINGLE_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART7_MULTIPLE_NUMBER_OF_SMALL_SETS = 100;
    private int smallSetId = 1;

    /**
     * PART1 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart1() throws IOException {
        for (int i = 1; i <= PART1_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART1);
            lc.setImage((int) (Math.random() * 50 + 1) + ".jpg");
            lc.setRecording("D1_0" + (int) (Math.random() * 5 + 1) + ".mp3");
            lc.setAnswer("A");
            lc.setExampleA("A");
            lc.setExampleB("B");
            lc.setExampleC("C");
            lc.setExampleD("D");
            lc.setSolution(aRandomSentence() + " " + aRandomSentence());
            questionRepository.save(lc);
        }
    }

    /**
     * PART2 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart2() throws IOException {
        for (int i = 1; i <= PART2_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART2);
            lc.setContent("Mark your answer on your answer sheet");
            lc.setRecording("D1_0" + (int) (Math.random() * 5 + 1) + ".mp3");
            lc.setAnswer("A");
            lc.setExampleA("A");
            lc.setExampleB("B");
            lc.setExampleC("C");
            lc.setSolution(aRandomSentence() + " " + aRandomSentence());
            questionRepository.save(lc);
        }
    }

    /**
     * PART3 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart3() throws IOException {
        for (int i = 1; i <= PART3_NUMBER_OF_SMALL_SETS; i++) {
            for (int j = 1; j <= 3; j++) {
                LC lc = new LC();
                lc.setQuestionType(QuestionType.PART3);
                lc.setRecording("D1_0" + (int) (Math.random() * 5 + 1) + ".mp3");
                lc.setContent(aRandomSentence());
                lc.setExampleA(aRandomSentence());
                lc.setExampleB(aRandomSentence());
                lc.setExampleC(aRandomSentence());
                lc.setExampleD(aRandomSentence());
                lc.setAnswer(aRandomSentence() + " " + aRandomSentence() + " " + aRandomSentence());
                lc.setAnswer("A");
                lc.setSmallSetId(smallSetId);
                lc.setSmallSetType(3);
                questionRepository.save(lc);
            }
            smallSetId++;
        }
    }

    /**
     * PART4 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart4() throws IOException {
        for (int i = 1; i <= PART4_NUMBER_OF_SMALL_SETS; i++) {
            for (int j = 1; j <= 3; j++) {
                LC lc = new LC();
                lc.setQuestionType(QuestionType.PART4);
                lc.setRecording("D1_0" + (int) (Math.random() * 5 + 1) + ".mp3");
                lc.setContent(aRandomSentence());
                lc.setExampleA(aRandomSentence());
                lc.setExampleB(aRandomSentence());
                lc.setExampleC(aRandomSentence());
                lc.setExampleD(aRandomSentence());
                lc.setSmallSetType(3);
                lc.setAnswer(aRandomSentence() + " " + aRandomSentence() + " " + aRandomSentence());
                lc.setAnswer("A");
                lc.setSmallSetId(smallSetId);
                questionRepository.save(lc);
            }
            smallSetId++;
        }
    }

    /**
     * PART5 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart5() throws IOException {
        Resource resource = new ClassPathResource("part6.CSV");
        List<RC> part5 = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .stream()
                .map(line -> {
                    String[] split = line.split("\\|");
                    RC rc = new RC();
                    rc.setQuestionType(QuestionType.PART5);
                    rc.setContent(split[2]);
                    rc.setAnswer(split[3]);
                    rc.setExampleA(split[4]);
                    rc.setExampleB(split[5]);
                    rc.setExampleC(split[6]);
                    rc.setExampleD(split[7]);
                    rc.setSolution(split[8]);
                    return rc;
                }).collect(Collectors.toList());
        questionRepository.saveAll(part5);
    }

    /**
     * PART6 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart6() throws IOException {
        for (int i = 1; i <= PART6_NUMBER_OF_SMALL_SETS; i++) {
            for (int j = 1; j <= 4; j++) {
                RC rc = new RC();
                rc.setQuestionType(QuestionType.PART6);
                rc.setContent(aRandomParagraph());
                rc.setExampleA(aRandomSentence());
                rc.setExampleB(aRandomSentence());
                rc.setExampleC(aRandomSentence());
                rc.setExampleD(aRandomSentence());
                rc.setAnswer("A");
                rc.setSolution(aRandomSentence() + aRandomSentence() + aRandomSentence() + aRandomSentence());
                rc.setSmallSetId(smallSetId);
                rc.setSmallSetType(4);
                questionRepository.save(rc);
            }
            smallSetId++;
        }
    }

    /**
     * PART7(단일지문) 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart7_single() throws IOException {
        for (int i = 1; i <= PART7_SINGLE_NUMBER_OF_SMALL_SETS; i++) {
            int smallSetType = (int) (Math.random() * 3 + 3);
            for (int j = 1; j <= smallSetType; j++) {
                RC rc = new RC();
                rc.setQuestionType(QuestionType.PART7_SINGLE_PARAGRAPH);
                rc.setContent(aRandomParagraph());
                rc.setSmallSetType(smallSetType);
                rc.setExampleA(aRandomSentence());
                rc.setExampleB(aRandomSentence());
                rc.setExampleC(aRandomSentence());
                rc.setExampleD(aRandomSentence());
                rc.setAnswer("A");
                rc.setSmallSetId(smallSetId);
                questionRepository.save(rc);
            }
            smallSetId++;
        }
    }

    /**
     * PART7(복합지문) 문제들을 생성합니다.
     * @throws IOException
     */
    public void initPart7_multiple() throws IOException {
        for (int i = 1; i <= PART7_MULTIPLE_NUMBER_OF_SMALL_SETS; i++) {
            int smallSetType = (int) (Math.random() * 3 + 3);
            for (int j = 1; j <= smallSetType; j++) {
                RC rc = new RC();
                rc.setQuestionType(QuestionType.PART7_MULTIPLE_PARAGRAPH);
                rc.setContent(aRandomParagraph());
                rc.setContent2(aRandomParagraph());
                if (Math.random() < 0.3) {
                    rc.setContent3(aRandomParagraph());
                }
                rc.setSmallSetType(smallSetType);
                rc.setExampleA(aRandomSentence());
                rc.setExampleB(aRandomSentence());
                rc.setExampleC(aRandomSentence());
                rc.setExampleD(aRandomSentence());
                rc.setAnswer("A");
                rc.setSmallSetId(smallSetId);
                questionRepository.save(rc);
            }
            smallSetId++;
        }
    }

    /**
     * 문제들을 lorem을 통해 무작위로 생성합니다.
     * @return
     * @throws IOException
     */
    public String aRandomSentence() throws IOException {
        Resource resource = new ClassPathResource("lorem-ipsum.txt");
        List<String> sentences = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);
        return sentences.get((int) (Math.random() * (sentences.size())));
    }

    /**
     * 무작위의 문단들을 생성합니다.
     * @return
     * @throws IOException
     */
    public String aRandomParagraph() throws IOException {
        StringBuilder paragraph = new StringBuilder();
        int num_of_sentence_in_paragraph = (int) (Math.random() * 5) + 5;
        for (int i = 1; i <= num_of_sentence_in_paragraph; ++i) {
            paragraph.append(aRandomSentence());
        }
        return paragraph.toString();
    }

    /**
     * DummyUser를 여러명 생성합니다.
     */
    public void createDummyUsers() {
        for (int i = 1; i <= NUMBER_OF_DUMMY_USERS; i++) {
            Member member = Member.builder()
                    .email("dummy" + i + "@a.a")
                    .userId("testUser" + i)
                    .password(passwordEncoder.encode("1234"))
                    .memberType(MemberType.USER)
                    .levelType(levelTypes[(int) (Math.random() * 3)])
                    .age((int) (Math.random() * 25) + 10)
                    .genderType(genderTypes[(int) (Math.random() * 2)])
                    .address(Address.builder().city("경기").street("일산").zipcode("10546").build())
                    .nickname("nickname" + i)
                    .build();

            memberRepository.save(member);

            long value = 1;
            for (long j : tagValues) {
                value *= Math.random() > 0.5 ? j : 1;
            }
            if (value == 1) {
                value = 2L * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23 * 29;
            }
            StudyGroupApplication studyGroupApplication = StudyGroupApplication.builder()
                    .member(memberRepository.findByEmail("dummy" + i + "@a.a"))
                    .value(value)
                    .build();
            studyGroupApplicationRepository.save(studyGroupApplication);
        }
    }

    /**
     * TestUser를 생성합니다.
     */
    public void createTestUsers() {
        Member member = Member.builder()
                .userId("testUser")
                .email("a@a.a")
                .password(passwordEncoder.encode("1234"))
                .memberType(MemberType.USER)
                .nickname("testUser")
                .levelType(levelTypes[(int) (Math.random() * 3)])
                .age((int) (Math.random() * 25) + 10)
                .genderType(genderTypes[(int) (Math.random() * 2)])
                .address(Address.builder().city("경기").street("일산").zipcode("10546").build())
                .build();

        memberRepository.save(member);
    }
    /**
     * TestUser와 DummyUser들을 생성합니다.
     */
    @PostConstruct
    public void initDummyUsers() {
        createDummyUsers();
        createTestUsers();
    }

    /**
     * 스터디 그룹을 무작위로 매칭합니다.
     */
    @PostConstruct
    public void testMatchStudyGroup() {
        studyGroupApplicationService.matchStudyGroups();
    }

    /**
     * 프로그램 실행시 PART1 ~ 7까지 문제들을 미리 생성합니다.
     * @throws IOException
     */
    @PostConstruct
    public void initQuestions() throws IOException {
        initPart1();
        initPart2();
        initPart3();
        initPart4();
        initPart5();
        initPart6();
        initPart7_single();
        initPart7_multiple();
    }

}
