package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toeicLab.toeicLab.domain.Question;
import toeicLab.toeicLab.domain.QuestionSet;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionSetService {
    private final int [] PART1 = {5};
    private final int [] PART2 = {8};
    private final int [] PART3 = {2};
    private final int [] PART4 = {2};

    private final QuestionService questionService;

    public QuestionSet createQuarterToeic() {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();
        for(int i=0; i< PART1[0]; i++){
            if (questionList.contains(questionService.createPart1())){
                continue;
            }
            questionList.add(questionService.createPart1());
        }
        for (int i=0; i<PART2[0];i++){
            if (questionList.contains(questionService.createPart2())){
                continue;
            }
            questionList.add(questionService.createPart2());
        }
        for (int i=0; i<PART3[0];i++){
            if (questionList.contains(questionService.createPart3())){
                continue;
            }
            questionList.add(questionService.createPart3());
        }

        result.setQuestions(questionList);
        return result;
    }
}
