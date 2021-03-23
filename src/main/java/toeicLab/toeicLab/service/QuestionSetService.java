package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionSetService {

    private final int [] PART1 = {3, 6, 6};
    private final int [] PART2 = {4, 8, 25};
    private final int [] PART3 = {3, 6, 13}; // set수, set당 3문제
    private final int [] PART4 = {3, 6, 10}; // set수, set당 3문제
    private final int [] PART5 = {4, 8, 30};
    private final int [] PART6 = {2, 4, 4}; // set수, set당 4문제

    private final QuestionSetRepository questionSetRepository;
    private final QuestionService questionService;

    public QuestionSet createToeicSet(Member member, QuestionSetType questionSetType) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();

        questionList.addAll(questionService.createQuestionList(QuestionType.PART1, PART1[questionSetType.get()]));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART2, PART2[questionSetType.get()]));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, PART3[questionSetType.get()]));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, PART4[questionSetType.get()]));

        questionList.addAll(questionService.createQuestionList(QuestionType.PART5, PART5[questionSetType.get()]));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, PART6[questionSetType.get()]));
        questionList.addAll(questionService.createPart7ByQuestionSetType(questionSetType));
        result.setQuestions(questionList);
        result.setCreatedAt(LocalDateTime.now());
        result.setMember(member);
        result.setQuestionSetType(questionSetType);
        questionSetRepository.save(result);
        return result;
    }


}
