package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList() {

        return questionRepository.findAll();
    }

    public List<Question> createQuestionList(QuestionType questionType, int numberOfQuestions) {
        List<Question> allQuestionList = questionRepository.findAllByQuestionType(questionType);
        List<Question> result = new ArrayList<>();

        for (int i = 1; i <= numberOfQuestions; ++i) {
            Question question = allQuestionList.get((int) (Math.random() * allQuestionList.size()));
            if (result.contains(question)) {
                --i;
                continue;
            }
            result.add(question);
        }

        return result;
    }

    public List<Question> createQuestionListWithSmallSet(QuestionType questionType, int numberOfSmallSets) {
        List<Question> allQuestionList = questionRepository.findAllByQuestionType(questionType);
        List<Question> result = new ArrayList<>();
        List<Question> duplicateCheckList = new ArrayList<>();
        for (int i = 1; i <= numberOfSmallSets; ++i) {
            Question question = allQuestionList.get((int) (Math.random() * allQuestionList.size()));
            if (!duplicateCheckList.contains(question)) {
                duplicateCheckList.add(question);
            }
            result.addAll(questionRepository.findAllBySmallSetId(question.getSmallSetId()));
        }
        return result;
    }

    public List<Question> createPart7ByQuestionSetType(QuestionSetType questionSetType) {
        int[] smallSet_Single = new int[3];
        int[] smallSet_Multiple = new int[3];

        switch (questionSetType.get()) {
            case 0: // quarter
                switch ((int) (Math.random() * 2)) {
                    case 0:
                        smallSet_Single = new int[]{0, 1, 1};
                        smallSet_Multiple = new int[]{0, 1, 0};
                        break;
                    case 1:
                        smallSet_Single = new int[]{1, 0, 1};
                        smallSet_Multiple = new int[]{0, 0, 1};
                        break;
                }
                break;

            case 1: // half
                switch ((int) (Math.random() * 3)) {
                    case 0:
                        smallSet_Single = new int[]{1, 1, 2};
                        smallSet_Multiple = new int[]{0, 1, 1};
                        break;
                    case 1:
                        smallSet_Single = new int[]{0, 4, 0};
                        smallSet_Multiple = new int[]{0, 0, 2};
                        break;
                    case 2:
                        smallSet_Single = new int[]{0, 2, 2};
                        smallSet_Multiple = new int[]{1, 0, 1};
                        break;
                }
                break;

            case 2: // full
                switch ((int) (Math.random() * 6)) {
                    case 0:
                        smallSet_Single = new int[]{0, 1, 5};
                        break;
                    case 1:
                        smallSet_Single = new int[]{3, 0, 4};
                        break;
                    case 2:
                        smallSet_Single = new int[]{2, 2, 3};
                        break;
                    case 3:
                        smallSet_Single = new int[]{1, 4, 2};
                        break;
                    case 4:
                        smallSet_Single = new int[]{5, 1, 2};
                        break;
                    case 5:
                        smallSet_Single = new int[]{4, 3, 1};
                        break;
                }
                switch ((int) (Math.random() * 4)) {
                    case 0:
                        smallSet_Multiple = new int[]{2, 2, 3};
                        break;
                    case 1:
                        smallSet_Multiple = new int[]{3, 0, 4};
                        break;
                    case 2:
                        smallSet_Multiple = new int[]{1, 3, 2};
                        break;
                    case 3:
                        smallSet_Multiple = new int[]{0, 1, 5};
                        break;
                }
                break;
        }

        List<Question> singleList = createPart7byQuestionTypeAndNumber(QuestionType.PART7_SINGLE_PARAGRAPH,
                smallSet_Single[0], smallSet_Single[1], smallSet_Single[2]);
        List<Question> multipleList = createPart7byQuestionTypeAndNumber(QuestionType.PART7_MULTIPLE_PARAGRAPH,
                smallSet_Multiple[0], smallSet_Multiple[1], smallSet_Multiple[2]);

        List<Question> result = new ArrayList<>(singleList);
        result.addAll(multipleList);
        return result;
    }

    public List<Question> createPart7byQuestionTypeAndNumber(QuestionType questionType, int numberOfSmallSetType3,
                                                             int numberOfSmallSetType4, int numberOfSmallSetType5) {
        List<Question> result = new ArrayList<>();

        for (int i = 1; i <= numberOfSmallSetType3; ++i) {
            List<Question> list = questionRepository.findAllByQuestionTypeAndSmallSetType(questionType, 3);
            Question question = list.get((int) (Math.random() * list.size()));
            if (result.contains(question)) {
                --i;
            }
            result.addAll(questionRepository.findAllBySmallSetId(question.getSmallSetId()));
        }

        for (int i = 1; i <= numberOfSmallSetType4; ++i) {
            List<Question> list = questionRepository.findAllByQuestionTypeAndSmallSetType(questionType, 4);
            Question question = list.get((int) (Math.random() * list.size()));
            if (result.contains(question)) {
                --i;
            }
            result.addAll(questionRepository.findAllBySmallSetId(question.getSmallSetId()));
        }

        for (int i = 1; i <= numberOfSmallSetType5; ++i) {
            List<Question> list = questionRepository.findAllByQuestionTypeAndSmallSetType(questionType, 5);
            Question question = list.get((int) (Math.random() * list.size()));
            if (result.contains(question)) {
                --i;
            }
            result.addAll(questionRepository.findAllBySmallSetId(question.getSmallSetId()));
        }
        return result;
    }

    public List<Question> createQuestionByQuestionTypeAndNumber(QuestionType questionType, int num) {
        List<Question> result = new ArrayList<>();

        for (int i = 1; i <= num; ++i) {
            List<Question> list = questionRepository.findAllByQuestionType(questionType);
            Question question = list.get((int) (Math.random() * list.size()));
            if (result.contains(question)) {
                --i;
            }
            result.addAll(questionRepository.findAllBySmallSetId(question.getSmallSetId()));
        }

        return result;
    }



}
