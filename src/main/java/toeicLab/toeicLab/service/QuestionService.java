package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;

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

    public List<Question> createQuestionList(QuestionType questionType,int numberOfQuestions) {
        List<Question> allQuestionList = questionRepository.findAllByQuestionType(questionType);
        List<Question> result = new ArrayList<>();

        for (int i=1; i<=numberOfQuestions; ++i){
            int ran = ((int)(Math.random()*allQuestionList.size()));
            if (result.contains(allQuestionList.get(ran))){
                --i;
                continue;
            }
            result.add(allQuestionList.get(ran));
        }

        return result;
    }

    public List<Question> createQuestionListWithSmallSet(QuestionType questionType, int numberOfSmallSets){
        List<Question> allQuestionList = questionRepository.findAllByQuestionType(questionType);
        List<Question> result = new ArrayList<>();
        List<Question> smallSetHead = new ArrayList<>();
        for (int i=1; i<= numberOfSmallSets; ++i){
            Question question = allQuestionList.get((int)(Math.random()*allQuestionList.size()));
            if (!smallSetHead.contains(question)){
                smallSetHead.add(question);

            }
            result.addAll(questionRepository.findAllBySmallSetId(smallSetHead.get(i).getSmallSetId()));
        }
        return result;
    }

    public List<Question> createPart7(QuestionSetType questionSetType){
        List<Question> result = new ArrayList<>();

        return result;
    }

}
