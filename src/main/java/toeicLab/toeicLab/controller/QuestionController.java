package toeicLab.toeicLab.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.service.MemberService;
import toeicLab.toeicLab.service.QuestionService;
import toeicLab.toeicLab.service.QuestionSetService;
import toeicLab.toeicLab.user.CurrentUser;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionSetRepository questionSetRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MemberService memberService;
    private final QuestionService questionService;
    private final QuestionSetService questionSetService;

    /**
     * [ToeicLab]의 모의고사 페이지로 이동합니다.
     * @param member
     * @param model
     * @return question/select_test
     */
    @GetMapping("/select_test")
    public String selectTest(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "question/select_test";
    }

    /**
     * 사용자가 선택한 연습문제(LR/RC/SPK)페이지로 이동합니다.
     * @param member
     * @param id
     * @param model
     * @return question/practice_select
     */
    @GetMapping("/practice_select/{id}")
    public String practiceTest(@CurrentUser Member member, @PathVariable String id, Model model){
        if(id.equals("rc")) model.addAttribute("practice", "rc");
        if(id.equals("lc")) model.addAttribute("practice", "lc");
        if(id.equals("spk")) model.addAttribute("practice", "spk");
        model.addAttribute("member", member);
        return "question/practice_select";
    }

    /**
     * 선택한 유형의 문제를 PART별로 입력한 개수만큼 가져옵니다.
     * @param member
     * @param id
     * @param request
     * @param model
     * @return question/practice_sheet
     */
    @RequestMapping("/practice/{id}")
    @Transactional
    public String test(@CurrentUser Member member, @PathVariable String id, HttpServletRequest request, Model model){
        model.addAttribute("member", member);
        QuestionSet list;
        if(("lc").equals(id)){
            int p1 = Integer.parseInt(request.getParameter("PART1"));
            int p2 = Integer.parseInt(request.getParameter("PART2"));
            int p3 = Integer.parseInt(request.getParameter("PART3"));
            int p4 = Integer.parseInt(request.getParameter("PART4"));
            if(p1 > 0) model.addAttribute("part1List", true);
            if(p2 > 0) model.addAttribute("part2List", true);
            if(p3 > 0) model.addAttribute("part3List", true);
            if(p4 > 0) model.addAttribute("part4List", true);
            list = questionSetService.createPracticeLC(member, p1, p2, p3, p4);
            questionSetRepository.save(list);
            model.addAttribute("questionList", list.getQuestions());
            model.addAttribute("questionSet", list);
            return "question/practice_sheet";
        }
        else if (("rc").equals(id)){
            int p5 = Integer.parseInt(request.getParameter("PART5"));
            int p6 = Integer.parseInt(request.getParameter("PART6"));
            int p7s = Integer.parseInt(request.getParameter("PART7-single"));
            int p7m = Integer.parseInt(request.getParameter("PART7-multiple"));
            if(p5 > 0) model.addAttribute("part5List", true);
            if(p6 > 0) model.addAttribute("part6List", true);
            if(p7s > 0) model.addAttribute("part7sList", true);
            if(p7m > 0) model.addAttribute("part7mList", true);
            list = questionSetService.createPracticeRC(member, p5, p6, p7s, p7m);
            questionSetRepository.save(list);
            model.addAttribute("questionList", list.getQuestions());
            model.addAttribute("questionSet", list);
            return "question/practice_sheet";
        }
        else {
            Meeting meeting = meetingRepository.getOne(Long.parseLong(id));
            List<QuestionSet> qsList = meeting.getQuestionSets();


            QuestionSet questionSet = new QuestionSet();

            for (QuestionSet qs : qsList){
                if (qs.getMember().getId().equals(member.getId())){
                    questionSet = qs;
                }
            }

            List<Question> questions = questionSet.getQuestions();
            for(Question q : questions){
                if(q.getQuestionType().toString().equals("PART1")) model.addAttribute("part1List", true);
                else if(q.getQuestionType().toString().equals("PART2")) model.addAttribute("part2List", true);
                else if(q.getQuestionType().toString().equals("PART3")) model.addAttribute("part3List", true);
                else if(q.getQuestionType().toString().equals("PART4")) model.addAttribute("part4List", true);
                else if(q.getQuestionType().toString().equals("PART5")) model.addAttribute("part5List", true);
                else if(q.getQuestionType().toString().equals("PART6")) model.addAttribute("part6List", true);
                else if(q.getQuestionType().toString().equals("PART7_SINGLE_PARAGRAPH")) model.addAttribute("part7sList", true);
                else if(q.getQuestionType().toString().equals("PART7_MULTIPLE_PARAGRAPH")) model.addAttribute("part7mList", true);
            }
            model.addAttribute("questionList", questions);
            model.addAttribute("questionSet", questionSet);
            model.addAttribute("studyGroupId", meeting.getStudyGroup().getId());
            return "question/practice_sheet";
        }
    }

    /**
     * 사용자가 원하는 세트의 모의고사를 풀 수 있는 페이지로 이동합니다.
     * @param member
     * @param type
     * @param model
     * @return question/test
     */
    @GetMapping("/test/{type}")
    @Transactional
    public String test1 (@CurrentUser Member member, @PathVariable String type, Model model){
        QuestionSet list = new QuestionSet();
        switch (type) {
            case "Quarter":
                list = questionSetService.createToeicSet(member, QuestionSetType.QUARTER_TOEIC);
                break;

            case "Half":
                list = questionSetService.createToeicSet(member, QuestionSetType.HALF_TOEIC);
                break;

            case "Full":
                list = questionSetService.createToeicSet(member, QuestionSetType.FULL_TOEIC);
                break;

            default:
                break;
        }
        questionSetRepository.save(list);
        model.addAttribute("questionSet", list);
        model.addAttribute("questionList", list.getQuestions());
        model.addAttribute("type", type);
        model.addAttribute("member", member);
        return "question/test";
    }

    /**
     * 사용자가 푼 문제의 결과를 보여줍니다.
     * @param member
     * @param request
     * @param questionSetId
     * @param model
     * @return question/result_sheet
     */
    @PostMapping("/result_sheet/{questionSetId}")
    public String resultSheet(@CurrentUser Member member, HttpServletRequest request, @PathVariable Long questionSetId, Model model) {
        QuestionSet questionSet = null;
        Map<Long, String> map = new HashMap<>();
        Enumeration en = request.getParameterNames();
        String param;
        String value;
        long setIdValue = 0;
        while (en.hasMoreElements()) {
            param = (String) en.nextElement();
            value = request.getParameter(param);
            if (param.equals("_csrf")) {
                continue;
            }
            if (param.equals("questionSetId")) {
                questionSet = questionSetRepository.getOne(Long.parseLong(value));
                setIdValue = Long.parseLong(value);
                continue;
            }
            if (param.equals("studyGroupId")) {
                model.addAttribute("studyGroupId", value);
                continue;
            }
            map.put(Long.parseLong(param), value);
        }
        assert questionSet != null;
        questionSet.setSubmittedAnswers(map);
        questionSetRepository.save(questionSet);
        questionSet = questionSetRepository.getOne(setIdValue);
        List<String> str = new ArrayList<>();
        questionService.checkTypeList(questionSet, str);
        model.addAttribute("questionType", str);
        model.addAttribute("questionList", questionSet.getQuestions());
        model.addAttribute("questionSet", questionSet);
        model.addAttribute("userAnswer", map);
        model.addAttribute("member", member);
        model.addAttribute("questionSetId", questionSetId);
        memberService.CreateLevelByAllQuestions(member);
        String[] eachstrings = questionSetService.getPercentageEachOfQuestionSet(questionSet);
        model.addAttribute("eachstrings", eachstrings);
        return "question/result_sheet";
    }

    /**
     * 사용자가 푼 문제들의 상세보기 페이지로 이동합니다.
     * @param member
     * @param id
     * @param request
     * @param model
     * @return question/detail
     */
    @RequestMapping( "/detail/{id}")
    public String lcAnswerSheet(@CurrentUser Member member, @PathVariable Long id, HttpServletRequest request, Model model) {
        long questionSetId = Long.parseLong(request.getParameter("questionSetId"));
        QuestionSet questionSet = questionSetRepository.getOne(questionSetId);

        Question question= questionService.findQuestion(id);

        if(member!=null){
            member = memberRepository.findByEmail(member.getEmail());
        }
        model.addAttribute("question", question);
        model.addAttribute("member", member);
        model.addAttribute("questionSetId", questionSetId);
        model.addAttribute("userAnswer", questionSet.getSubmittedAnswers());
        return "question/detail";
    }


}
