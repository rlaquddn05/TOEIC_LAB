package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MailRepository;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;
import toeicLab.toeicLab.service.MailService;
import toeicLab.toeicLab.service.MemberService;
import toeicLab.toeicLab.service.QuestionService;
import toeicLab.toeicLab.service.QuestionSetService;
import toeicLab.toeicLab.user.CurrentUser;
import toeicLab.toeicLab.user.SignUpForm;
import toeicLab.toeicLab.user.SignUpValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final MailService mailService;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final QuestionSetService questionSetService;
    private final QuestionSetRepository questionSetRepository;
    private final QuestionService questionService;

    @GetMapping("/")
    public String index(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        return "/view/index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "/view/login";
    }

    @GetMapping("/send_reset_password_link")
    public String sendResetPasswordView() {
        return "/view/send_reset_password_link";
    }

    @PostMapping("/send_reset_password_link")
    public String sendResetPassword(String userId, String email, Model model) {
        try {
            Member checkedMember = memberService.sendResetPasswordEmail(userId, email);
            model.addAttribute("checkedMember", checkedMember);
            MailDto mailDto = new MailDto();
            mailDto.setEmail(checkedMember.getEmail());
            mailDto.setEmailCheckToken(mailDto.generateEmailCheckToken());

            MailDto resetPasswordEmail = mailService.resetPasswordMailSend(mailDto);
            mailRepository.save(resetPasswordEmail);

            model.addAttribute("resetPasswordEmail", resetPasswordEmail);


        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "password.reset.failed");
            return "/view/notify_password";
        }

        model.addAttribute("result_code", "password.reset.send");
        return "/view/notify_password";
    }

    @GetMapping("/reset/checkTokens")
    @ResponseBody
    public String resetCheckTokens(@RequestParam("resetPasswordEmailToken") String resetPasswordEmailToken,
                                   @RequestParam("resetPasswordEmail") String resetPasswordEmail){

        JsonObject jsonObject = new JsonObject();
        MailDto getResetPasswordTokenMail = mailRepository.findByEmail(resetPasswordEmail);
        log.info(getResetPasswordTokenMail.getEmail());
        boolean result = false;

        result = getResetPasswordTokenMail.getEmailCheckToken().equals(resetPasswordEmailToken);

        if (result) {
            jsonObject.addProperty("message", "비밀번호 초기화 인증 성공");
        } else {
            jsonObject.addProperty("message", "비밀번호 초기화 인증번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }



    @GetMapping("/notify_password")
    public String notifyPasswordView(){
        return "/view/notify_password";
    }

    @PostMapping("/notify_password")
    public String goResetPassword(@RequestParam("resetPasswordEmailToken")String resetPasswordEmailToken, Model model){
        MailDto mailByEmailCheckToken = mailRepository.findByEmailCheckToken(resetPasswordEmailToken);
        model.addAttribute("email", mailByEmailCheckToken.getEmail());
        return "/view/reset_password";
    }
//      @GetMapping("/reset_password")
//      public String resetPasswordView(){
//        return "/view/reset_password";
//      }
    @GetMapping("reset_password")
    public String resetPasswordView() {
        return "/view/reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(String email, String password, Model model) {
        memberService.resetPassword(email, password);
        model.addAttribute("result_code", "password.reset.success");
        return "/view/notify_password";
    }

    @GetMapping("/send_find_id_link")
    public String sendFindIdView() {
        return "/view/send_find_id_link";
    }

    @PostMapping("/send_find_id_link")
    public String sendFindId(String email, Model model) {
        try {
            memberService.sendFindIdByEmail(email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "find.id.failed");
            return "/view_find_id";
        }
        model.addAttribute("success_code", "find.id.success");
        return "/view/find_id";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute(new SignUpForm());
        return "view/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            log.info("유효성 에러 발생!");
            return "/view/signup";
        }
        signUpValidator.validate(signUpForm, errors);
        log.info("유효성 검사 끝!");

        Member member = memberService.createNewMember(signUpForm);

        memberService.autologin(member); // 해당 멤버를 자동 로그인 해주기

        return "redirect:/";
    }

    @GetMapping("/signup/email")
    @ResponseBody
    public String sendEmailCheckToken(@RequestParam("email") String email) {
        MailDto mailDto = new MailDto();
        mailDto.setEmail(email);
        mailDto.setEmailCheckToken(mailDto.generateEmailCheckToken());

        MailDto saveMail = mailService.mailSend(mailDto);
        mailRepository.save(saveMail);

        JsonObject jsonObject = new JsonObject();

        boolean result = false;
        result = saveMail != null;

        if (result) {
            jsonObject.addProperty("message", "이메일을 확인하세요.");
        } else {
            jsonObject.addProperty("message", "올바른 이메일형식이 아닙니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/signup/checkTokens")
    @ResponseBody
    public String checkTokens(@RequestParam("email") String email, @RequestParam("certification_number") String certification_number) {
        log.info(certification_number);

        JsonObject jsonObject = new JsonObject();
        MailDto getTokenMail = mailRepository.findByEmail(email);
        log.info(getTokenMail.getEmail());
        boolean result = false;

        result = getTokenMail.getEmailCheckToken().equals(certification_number);

        if (result) {
            jsonObject.addProperty("message", "이메일 인증 성공");
        } else {
            jsonObject.addProperty("message", "이메일 인증번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/signup/checkUserId")
    @ResponseBody
    public String checkUserId(@RequestParam("userId") String userId) {

        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = memberRepository.existsByUserId(userId);

        if (result) {
            jsonObject.addProperty("message", "이미 존재하는 아이디입니다.");
        } else {
            jsonObject.addProperty("message", "사용 가능한 아이디입니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/signup/checkNickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname) {
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = memberRepository.existsByNickname(nickname);

        if (result) {
            jsonObject.addProperty("message", "이미 존재하는 닉네임입니다.");
        } else {
            jsonObject.addProperty("message", "사용 가능한 닉네임입니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/signup/checkPasswords")
    @ResponseBody
    public String checkPasswords(@RequestParam("password") String password, @RequestParam("check_password") String check_password) {
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = password.equals(check_password);

        if (result) {
            jsonObject.addProperty("message", "비밀번호가 일치합니다,");
        } else {
            jsonObject.addProperty("message", "비밀번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/my_page")
    public String myPage(@CurrentUser Member member, Model model) {
        Member user = memberRepository.findByEmail(member.getEmail());
        model.addAttribute("userDto", user);
        return "/view/my_page";
    }

    @GetMapping("/my_progress")
    public String myProgress() {
        return "/view/my_progress";
    }

    @GetMapping("/my_studygroup_list")
    public String myStudyGroupList() {
        return "/view/my_studygroup_list";
    }

    @GetMapping("/my_studygroup")
    public String myStudyGroup() {
        return "/view/my_studygroup";
    }

    @GetMapping("/create_meeting")
    public String createMeeting() {
        return "/view/create_meeting";
    }

    @GetMapping("/my_vocabulary_list")
    public String myVocabularyList() {
        return "/view/my_vocabulary_list";
    }

    @GetMapping("/toeiclab_introduction")
    public String toeiclabIntroduction() {
        return "/view/toeiclab_intro";
    }

    @GetMapping("/select_test")
    public String selectTest() {
        return "/view/select_test";
    }

    @GetMapping("/rc_sheet")
    public String rcSheet() {
        return "/view/rc_sheet";
    }

    @GetMapping("/lc_sheet")
    public String lcSheet() {
        return "practice_sheet";
    }

    @GetMapping("/spk_sheet")
    public String spkSheet() {
        return "/view/spk_sheet";
    }

    @GetMapping("/spk_confirm_answer")
    public String spkConfirmAnswer() {
        return "/view/spk_confirm_answer";
    }

    @GetMapping("/apply_studygroup")
    public String applyStudygroup() {
        return "/view/apply_studygroup";
    }

    @PostMapping("/apply_studygroup")
    public String SubmitStudyGroupApplication(@CurrentUser Member member, Model model){
        return "/view/index";
    }

    @PostMapping("/result_sheet")
    public String resultSheet(HttpServletRequest request) {
        QuestionSet questionSet = null;
        String [] str = {"correct", "wrong", "none"};
        List<String> checkAnswer = new ArrayList<>();
        Enumeration en = request.getParameterNames();
        String param;
        String value;
        Map<Integer, String> map = new HashMap<>();
        while (en.hasMoreElements()){
            param = (String)en.nextElement();
            value = request.getParameter(param);
            if(param.equals("_csrf")){
                continue;
            }
            if(param.equals("questionSetId")){
                questionSet = questionSetRepository.getOne(Long.parseLong(value));
                continue;
            }
            map.put(Integer.parseInt(param), value);

            System.out.println(param + "-" +value);
        }
        assert questionSet != null;
        questionSet.setSubmittedAnswers(map);
        questionSetRepository.save(questionSet);
        /*============================================================================*/


        return "/view/result_sheet";
    }

    @GetMapping("/my_review_note")
    public String myReviewNote() {
        return "/view/my_review_note";
    }

    @GetMapping("/rc_answer_sheet")
    public String rcAnswerSheet() {
        return "/view/rc_answer_sheet";
    }

    @GetMapping("/lc_answer_sheet")
    public String lcAnswerSheet() {
        return "/view/lc_answer_sheet";
    }

    @GetMapping("/spk_answer_sheet")
    public String spkAnswerSheet() {
        return "/view/spk_answer_sheet";
    }

    @GetMapping("/vocabulary_test")
    public String vocabularyTest() {
        return "/view/vocabulary_test";
    }

    @PostMapping("/popup_dictionary")
    public String popupDictionary() {
        return "/view/popup_dictionary";
    }

    @GetMapping("/forum")
    public String forum() {
        return "/view/forum";
    }

    @GetMapping("/schedule")
    public String schedule() {
        return "/view/schedule";
    }


    @GetMapping("/practice_select/{id}")
    public String practiceTest(@PathVariable String id, Model model){
        if(id.equals("rc")) model.addAttribute("practice", "rc");
        if(id.equals("lc")) model.addAttribute("practice", "lc");
        if(id.equals("spk")) model.addAttribute("practice", "spk");
        return "/view/practice_select";
    }

    @RequestMapping("/practice/{id}")
    @Transactional
    public String test(@CurrentUser Member member, @PathVariable String id, HttpServletRequest request, Model model){
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
            return "/view/practice_sheet";
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
            return "/view/practice_sheet";
        }
        else {
            System.out.println("partspk : " + request.getParameter("PARTspk"));
            return "/view/spk_sheet";
        }
    }

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
            return "/view/question/test";
        }

    }