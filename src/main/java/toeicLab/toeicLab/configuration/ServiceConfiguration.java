package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.service.StudyGroupApplicationService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Profile("service")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ServiceConfiguration {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    private final StudyGroupApplicationService studyGroupApplicationService;
    private static final LevelType[] levelTypes = {LevelType.BEGINNER,
            LevelType.INTERMEDIATE, LevelType.ADVANCED};
    private static final GenderType[] genderTypes = {GenderType.MALE, GenderType.FEMALE};

    private static final int PART1_NUMBER = 100;
    private static final int PART2_NUMBER = 100;
    private static final int PART3_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART4_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART6_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART7_SINGLE_NUMBER_OF_SMALL_SETS = 100;
    private static final int PART7_MULTIPLE_NUMBER_OF_SMALL_SETS = 100;
    private int smallSetId = 1;

    /**
     * 오전 04시 마다 사용자들의 신청서를 검토한 뒤 자동으로 매칭합니다.
     */
    @Scheduled(cron="0 0 04 * * ?")
    public void MatchStudyGroup() {
        studyGroupApplicationService.matchStudyGroups();
    }

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
     * TestUser를 생성합니다.
     */
    @PostConstruct
    public void createTestUsers() {
        Member member = Member.builder()
                .userId("testUser")
                .email("a@a.a")
                .password("1234")
                .memberType(MemberType.USER)
                .levelType(levelTypes[(int) (Math.random() * 3)])
                .age((int) (Math.random() * 25) + 10)
                .genderType(genderTypes[(int) (Math.random() * 2)])
                .build();

        memberRepository.save(member);
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

    /**
     * TestUser를 여러명 미리 생성합니다.
     */
    @PostConstruct
    public void initDummyUsers() {
        createTestUsers();
    }
}
