package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.LC;
import toeicLab.toeicLab.domain.Question;
import toeicLab.toeicLab.domain.QuestionType;
import toeicLab.toeicLab.domain.RC;
import toeicLab.toeicLab.repository.LCRepository;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.repository.RCRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {

    private final int PART1_NUMBER = 10;
    private final int PART2_NUMBER = 10;
    private final int PART3_NUMBER = 10;
    private final int PART4_NUMBER = 10;
    //    private final int PART5_NUMBER= 10;
    private final int PART6_NUMBER = 10;
    private final int PART7_SINGLE_NUMBER_OF_SMALL_SETS = 10;
    private final int PART7_MULTIPLE_NUMBER_OF_SMALL_SETS = 10;
    private final QuestionRepository questionRepository;
    private final RCRepository rcRepository;
    private final LCRepository lcRepository;

    private int smallSetId = 1;

    @PostConstruct
    public void initPart1() throws IOException {
        for (int i = 1; i <= PART1_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART1);
            lc.setImage((int) (Math.random() * 50 + 1) + ".jpg");
            lc.setAnswer("A");
            lc.setExampleA("A");
            lc.setExampleB("B");
            lc.setExampleC("C");
            lc.setExampleD("D");
            lc.setSolution(aRandomSentence()+" "+aRandomSentence());
            questionRepository.save(lc);
        }
    }

    @PostConstruct
    public void initPart2() throws IOException {
        for (int i = 1; i <= PART2_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART2);
            lc.setContent("Mark your answer on your answer sheet");
            lc.setAnswer("A");
            lc.setExampleA("A");
            lc.setExampleB("B");
            lc.setExampleC("C");
            lc.setSolution(aRandomSentence()+" "+aRandomSentence());
            questionRepository.save(lc);
        }
    }

    @PostConstruct
    public void initPart3() throws IOException {
        Resource resource = new ClassPathResource("lorem-ipsum.txt");
        List<String> sentences = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);

        for (int i = 1; i <= PART3_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART3);
            lc.setContent(aRandomSentence());
            lc.setExampleA(aRandomSentence());
            lc.setExampleB(aRandomSentence());
            lc.setExampleC(aRandomSentence());
            lc.setExampleD(aRandomSentence());
            lc.setAnswer(aRandomSentence() + " " + aRandomSentence() + " " + aRandomSentence());
            lc.setAnswer("A");
            questionRepository.save(lc);

        }
    }

    @PostConstruct
    public void initPart4() throws IOException {
        Resource resource = new ClassPathResource("lorem-ipsum.txt");
        List<String> sentences = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);

        for (int i = 1; i <= PART4_NUMBER; i++) {
            LC lc = new LC();
            lc.setQuestionType(QuestionType.PART4);
            lc.setContent(sentences.get((int) (Math.random() * (sentences.size()))));
            lc.setExampleA(sentences.get((int) (Math.random() * (sentences.size()))));
            lc.setExampleB(sentences.get((int) (Math.random() * (sentences.size()))));
            lc.setExampleC(sentences.get((int) (Math.random() * (sentences.size()))));
            lc.setExampleD(sentences.get((int) (Math.random() * (sentences.size()))));
            lc.setAnswer("A");
            questionRepository.save(lc);

        }
    }

    @PostConstruct
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

    @PostConstruct
    public void initPart6() throws IOException {
        for (int i = 1; i <= PART6_NUMBER; i++) {
            RC rc = new RC();
            rc.setQuestionType(QuestionType.PART6);
            rc.setContent(aRandomParagraph());
            rc.setExampleA(aRandomSentence());
            rc.setExampleB(aRandomSentence());
            rc.setExampleC(aRandomSentence());
            rc.setExampleD(aRandomSentence());
            rc.setAnswer("A");
            rc.setSolution(aRandomSentence() + aRandomSentence() + aRandomSentence() + aRandomSentence());
            questionRepository.save(rc);

        }
    }

    @PostConstruct
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
            }
            smallSetId++;
        }
    }

    @PostConstruct
    public void initPart7_multiple() throws IOException {
        for (int i = 1; i <= PART7_MULTIPLE_NUMBER_OF_SMALL_SETS; i++) {
            int smallSetType = (int) (Math.random() * 3 + 3);
            for (int j = 1; j <= smallSetType; j++) {
                RC rc = new RC();
                rc.setQuestionType(QuestionType.PART7_SINGLE_PARAGRAPH);
                rc.setContent(aRandomParagraph());
                rc.setContent2(aRandomParagraph());
                if(Math.random()<0.3){
                    rc.setContent3(aRandomParagraph());
                }
                rc.setSmallSetType(smallSetType);
                rc.setExampleA(aRandomSentence());
                rc.setExampleB(aRandomSentence());
                rc.setExampleC(aRandomSentence());
                rc.setExampleD(aRandomSentence());
                rc.setAnswer("A");
                rc.setSmallSetId(smallSetId);
            }
            smallSetId++;
        }
    }


    public String aRandomSentence() throws IOException {
        Resource resource = new ClassPathResource("lorem-ipsum.txt");
        List<String> sentences = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);
        return sentences.get((int) (Math.random() * (sentences.size())));
    }

    public String aRandomParagraph() throws IOException {
        Resource resource = new ClassPathResource("lorem-ipsum.txt");
        List<String> sentences = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);

        String paragraph = "";
        int num_of_sentence_in_paragraph = (int) (Math.random() * 5) + 5;
        for (int i = 1; i <= num_of_sentence_in_paragraph; ++i) {
            paragraph += aRandomSentence();
        }
        return paragraph;
    }


    public List<RC> getRCList(){
        return rcRepository.findAll();
    }

    public List<LC> getLCList(){
        return lcRepository.findAll();
    }

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question createPart1() {
        List<Question> part1 = new ArrayList<>();
        for (Question q : getLCList()){
            if(q.getQuestionType().equals(QuestionType.PART1)){
                part1.add(q);
            }
        }

        return  part1.get((int)(Math.random()*part1.size()));
    }

    public Question createPart2() {
        List<Question> part2 = new ArrayList<>();
        for (Question q : getLCList()){
            if(q.getQuestionType().equals(QuestionType.PART2)){
                part2.add(q);
            }
        }

        return  part2.get((int)(Math.random()*part2.size()));
    }

    public Question createPart3() {
        List<Question> part3 = new ArrayList<>();
        for (Question q : getLCList()){
            if(q.getQuestionType().equals(QuestionType.PART3)){
                part3.add(q);
            }
        }
        return part3.get((int)(Math.random()*part3.size()));
    }

}
