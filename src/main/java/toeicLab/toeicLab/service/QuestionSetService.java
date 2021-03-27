package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MeetingRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final MeetingRepository meetingRepository;

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

        return result;
    }


    public QuestionSet createPracticeLC(Member member, int p1, int p2, int p3, int p4) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();
        if(p1 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART1, p1));
        if(p2 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART2, p2));
        if(p3 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, p3));
        if(p4 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, p4));
        result.setQuestions(questionList);
        result.setCreatedAt(LocalDateTime.now());
        result.setMember(member);
        result.setQuestionSetType(QuestionSetType.PRACTICE);
        return result;
    }

    public QuestionSet createPracticeRC(Member member, int p5, int p6, int p7s, int p7m) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();
        if(p5 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART5, p5));
        if(p6 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, p6));
        if(p7s > 0) questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_SINGLE_PARAGRAPH, p7s));
        if(p7m > 0) questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_MULTIPLE_PARAGRAPH, p7m));
        result.setQuestions(questionList);
        result.setCreatedAt(LocalDateTime.now());
        result.setMember(member);
        result.setQuestionSetType(QuestionSetType.PRACTICE);
        return result;
    }

<<<<<<< HEAD
    public void createMeeting(StudyGroup studyGroup, String date){
        QuestionSet meetingQuestionSet = new QuestionSet();
        List<Question> questionList = new ArrayList<>();

        int part1 = 0;
        int part2 = 0;
        int part3 = 0;
        int part4 = 0;
        int part5 = 0;
        int part6 = 0;
        int part7_Single = 0;
        int part7_Multiple = 0;

        questionList.addAll(questionService.createQuestionList(QuestionType.PART1, part1));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART2, part2));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, part3));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, part4));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART5, part5));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, part6));
        questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_SINGLE_PARAGRAPH, part7_Single));
        questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_MULTIPLE_PARAGRAPH, part7_Multiple));

        meetingQuestionSet.setQuestions(questionList);
        meetingQuestionSet.setCreatedAt(LocalDateTime.now());
        meetingQuestionSet.setQuestionSetType(QuestionSetType.MEETING);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime = LocalDate.parse(date, formatter);

        Meeting meeting = Meeting.builder()
                .studyGroup(studyGroup)
                .date(dateTime.atStartOfDay())
                .build();
        meetingRepository.save(meeting);


        for (Member m: studyGroup.getMembers()){
            meetingQuestionSet.setMember(m);
            questionSetRepository.save(meetingQuestionSet);
        }
=======
    public QuestionSet findQuestionSet(Long setId) {
        Optional<QuestionSet> optional= questionSetRepository.findById(setId);
        return optional.orElse(null);
>>>>>>> ac9b1f316d6c75f9c6b9ecab76838de5f0c58b1f
    }
}
