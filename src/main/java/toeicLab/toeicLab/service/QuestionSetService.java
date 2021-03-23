package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toeicLab.toeicLab.domain.Question;
import toeicLab.toeicLab.domain.QuestionSet;
import toeicLab.toeicLab.domain.QuestionSetType;
import toeicLab.toeicLab.domain.QuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionSetService {

    private final int [] PART1 = {3, 6, 6};
    private final int [] PART2 = {4, 8, 25};
    private final int [] PART3 = {3, 6, 13}; // set수, set당 3문제
    private final int [] PART4 = {3, 6, 10}; // set수, set당 3문제
    private final int [] PART5 = {4, 8, 30};
    private final int [] PART6 = {2, 4, 4}; // set수, set당 4문제
    private final int [] PART7_SINGLE = {2, 4};// set수, set당 3~5문제
    private final int [] PART7_MULTIPLE = {1, 2};// set수, set당 3~5문제


    private final QuestionService questionService;

    public QuestionSet createToeicSet(QuestionSetType questionSetType) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();

        questionList.addAll(questionService.createQuestionList(QuestionType.PART1, PART1[questionSetType.get()]));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART2, PART2[questionSetType.get()]));
//        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, PART3[questionSetType.get()]));
//        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, PART4[questionSetType.get()]));

        questionList.addAll(questionService.createQuestionList(QuestionType.PART5, PART5[questionSetType.get()]));
//        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, PART6[questionSetType.get()]));
//        questionList.addAll(questionService.createPart7(questionSetType));
        result.setQuestions(questionList);
        return result;
    }
}
