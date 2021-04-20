package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MeetingRepository;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionSetService {

    private final int[] PART1 = {3, 6, 6};
    private final int[] PART2 = {4, 8, 25};
    private final int[] PART3 = {3, 6, 13};
    private final int[] PART4 = {3, 6, 10};
    private final int[] PART5 = {4, 8, 30};
    private final int[] PART6 = {2, 4, 4};

    private final QuestionSetRepository questionSetRepository;
    private final QuestionService questionService;
    private final MeetingRepository meetingRepository;
    private final QuestionRepository questionRepository;

    /**
     * 모의고사 형태로 문제를 형성한다.
     * @param member
     * @param questionSetType
     * @return result
     */
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

    /**
     * LC문제들을 형성한다.
     * @param member
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return result
     */
    public QuestionSet createPracticeLC(Member member, int p1, int p2, int p3, int p4) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();
        if (p1 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART1, p1));
        if (p2 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART2, p2));
        if (p3 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, p3));
        if (p4 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, p4));
        result.setQuestions(questionList);
        result.setCreatedAt(LocalDateTime.now());
        result.setMember(member);
        result.setQuestionSetType(QuestionSetType.PRACTICE);

        return result;
    }

    /**
     * RC문제들을 형성한다.
     * @param member
     * @param p5
     * @param p6
     * @param p7s
     * @param p7m
     * @return result
     */
    public QuestionSet createPracticeRC(Member member, int p5, int p6, int p7s, int p7m) {
        QuestionSet result = new QuestionSet();
        List<Question> questionList = new ArrayList<>();
        if (p5 > 0) questionList.addAll(questionService.createQuestionList(QuestionType.PART5, p5));
        if (p6 > 0) questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, p6));
        if (p7s > 0)
            questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_SINGLE_PARAGRAPH, p7s));
        if (p7m > 0)
            questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_MULTIPLE_PARAGRAPH, p7m));
        result.setQuestions(questionList);
        result.setCreatedAt(LocalDateTime.now());
        result.setMember(member);
        result.setQuestionSetType(QuestionSetType.PRACTICE);

        return result;
    }

    /**
     * 스터디를 형성한다.
     * @param studyGroup
     * @param numberOfQuestions
     * @param date
     */
    public void createMeeting(StudyGroup studyGroup, int[] numberOfQuestions, String date) {
        List<Question> questionList = new ArrayList<>();

        int part1 = numberOfQuestions[0];
        int part2 = numberOfQuestions[1];
        int part3 = numberOfQuestions[2];
        int part4 = numberOfQuestions[3];
        int part5 = numberOfQuestions[4];
        int part6 = numberOfQuestions[5];
        int part7_Single = numberOfQuestions[6];
        int part7_Multiple = numberOfQuestions[7];

        questionList.addAll(questionService.createQuestionList(QuestionType.PART1, part1));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART2, part2));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART3, part3));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART4, part4));
        questionList.addAll(questionService.createQuestionList(QuestionType.PART5, part5));
        questionList.addAll(questionService.createQuestionListWithSmallSet(QuestionType.PART6, part6));
        questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_SINGLE_PARAGRAPH, part7_Single));
        questionList.addAll(questionService.createQuestionByQuestionTypeAndNumber(QuestionType.PART7_MULTIPLE_PARAGRAPH, part7_Multiple));

        List <QuestionSet> questionSets = new ArrayList<>();
        Meeting meeting = new Meeting();
        meeting.setStudyGroup(studyGroup);

        for (int i = 0; i < studyGroup.getMembers().size(); ++i){
            QuestionSet meetingQuestionSet = new QuestionSet();

            meetingQuestionSet.setQuestions(questionList);
            meetingQuestionSet.setCreatedAt(LocalDateTime.now());
            meetingQuestionSet.setQuestionSetType(QuestionSetType.MEETING);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(date, formatter);
            meeting.setDate(dateTime.atStartOfDay());

            meetingQuestionSet.setMember(studyGroup.getMembers().get(i));
            questionSetRepository.save(meetingQuestionSet);

            questionSets.add(meetingQuestionSet);
            meeting.setQuestionSets(questionSets);
            meetingRepository.save(meeting);
        }
    }

    /**
     * 원하는 PART의 문제를 형성한다.
     * @param select_form
     * @return result
     */
    public int[] selectFormToArray(String[] select_form) {
        int[] result = new int[8];

        for (int i = 1; i < select_form.length; i = i + 3) {
            switch (select_form[i]) {
                case "PART1":
                    result[0] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART2":
                    result[1] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART3":
                    result[2] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART4":
                    result[3] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART5":
                    result[4] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART6":
                    result[5] = Integer.parseInt(select_form[i + 1]);
                    break;
                case "PART7":
                    result[6] = (int) (Math.random() * Integer.parseInt(select_form[i + 1]));
                    result[7] = Integer.parseInt(select_form[i + 1]) - result[6];
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    /**
     * 제출된 정답의 개수에 따라 정답률을 계산한다.
     * @param questionSet
     * @return %
     */
    public String getPercentage(QuestionSet questionSet) {
        int correctCount = 0;
        int totalCount = questionSet.getQuestions().size();
        Map<Long, String> submittedAnswersForQs = questionSet.getSubmittedAnswers();
        for (Map.Entry<Long, String> entry : submittedAnswersForQs.entrySet()) {
            Question question = questionRepository.getOne(entry.getKey());
            if (question.getAnswer().equals(entry.getValue())) {
                correctCount++;
            }
        }
        String str = Integer.toString(correctCount * 100 / totalCount);
        return str + "%";
    }

    /**
     * 각 문제세트에 따라 각각의 정답률을 계산하고 comment를 제공한다.
     * @param questionSet
     * @return comment
     */
    public String[] getPercentageEachOfQuestionSet(QuestionSet questionSet) {
        String[] comments = new String[8];

        int[] part1 = new int[2];
        int[] part2 = new int[2];
        int[] part3 = new int[2];
        int[] part4 = new int[2];
        int[] part5 = new int[2];
        int[] part6 = new int[2];
        int[] part7 = new int[2];

        Map<Long, String> submittedAnswersForQs = questionSet.getSubmittedAnswers();
        for (Map.Entry<Long, String> entry : submittedAnswersForQs.entrySet()) {
            Question question = questionRepository.getOne(entry.getKey());
            if (question.getQuestionType().toString().equals("PART1")) {
                part1[0]++;
                if (question.getAnswer().equals(entry.getValue())) part1[1]++;
            } else if (question.getQuestionType().toString().equals("PART2")) {
                part2[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part2[1]++;
            } else if (question.getQuestionType().toString().equals("PART3")) {
                part3[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part3[1]++;
            } else if (question.getQuestionType().toString().equals("PART4")) {
                part4[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part4[1]++;
            } else if (question.getQuestionType().toString().equals("PART5")) {
                part5[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part5[1]++;
            } else if (question.getQuestionType().toString().equals("PART6")) {
                part6[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part6[1]++;
            } else if (question.getQuestionType().toString().equals("PART7_SINGLE_PARAGRAPH") || question.getQuestionType().toString().equals("PART7_MULTIPLE_PARAGRAPH")) {
                part7[0]++;
                if ((question.getAnswer().equals(entry.getValue()))) part7[1]++;
            }
        }

        comments[0] = getPercentage(questionSet);

        try {
            comments[1] = part1[1] + " / " + part1[0] + " = " + (part1[1] * 100 / part1[0]) + "%";
        } catch (ArithmeticException e) {
            comments[1] = null;
        }
        try {
            comments[2] = part2[1] + " / " + part2[0] + " = " + (part2[1] * 100 / part2[0]) + "%";
        } catch (ArithmeticException e) {
            comments[2] = null;
        }
        try {
            comments[3] = part3[1] + " / " + part3[0] + " = " + (part3[1] * 100 / part3[0]) + "%";
        } catch (ArithmeticException e) {
            comments[3] = null;
        }
        try {
            comments[4] = part4[1] + " / " + part4[0] + " = " + (part4[1] * 100 / part4[0]) + "%";
        } catch (ArithmeticException e) {
            comments[4] = null;
        }
        try {
            comments[5] = part5[1] + " / " + part5[0] + " = " + (part5[1] * 100 / part5[0]) + "%";
        } catch (ArithmeticException e) {
            comments[5] = null;
        }
        try {
            comments[6] = part6[1] + " / " + part6[0] + " = " + (part6[1] * 100 / part6[0]) + "%";
        } catch (ArithmeticException e) {
            comments[6] = null;
        }
        try {
            comments[7] = part7[1] + " / " + part7[0] + " = " + (part7[1] * 100 / part7[0]) + "%";
        } catch (ArithmeticException e) {
            comments[7] = null;
        }

        return comments;
    }
}
