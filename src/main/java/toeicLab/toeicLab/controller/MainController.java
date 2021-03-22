package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.QuestionSet;
import toeicLab.toeicLab.service.MemberService;
import toeicLab.toeicLab.service.QuestionSetService;
import toeicLab.toeicLab.user.CurrentUser;
import toeicLab.toeicLab.user.SignUpForm;
import toeicLab.toeicLab.user.SignUpValidator;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.user.CurrentUser;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final QuestionSetService questionSetService;

    @GetMapping("/")
    public String index(){
        return "/view/index";
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "/view/login";
    }

    @GetMapping("/send_reset_password_link")
    public String sendResetPasswordView(){
        return "/view/send_reset_password_link";
    }

    @PostMapping("/send_reset_password_link")
    public String sendResetPassword(){
        return "";
    }

    @GetMapping("/send_find_id_link")
    public String sendFindIdView(){
        return "/view/send_find_id_link";
    }

    @PostMapping("/send_find_id_link")
    public String sendFindId(){
        return "";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute(new SignUpForm());
        return "/view/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            log.info("유효성 에러 발생!");
            return "/view/signup";
        }
        signUpValidator.validate(signUpForm, errors);
        log.info("유효성 검사 끝!");
        Member member = memberService.createNewMember(signUpForm);
//        memberService.sendSignUpEmail(member);
        return "redirect:/";
    }


    @GetMapping("/my_page")
    public String myPage(){
        return "/view/my_page";
    }

    @GetMapping("/my_progress")
    public String myProgress(){
        return "/view/my_progress";
    }

    @GetMapping("/my_studygroup_list")
    public String myStudyGroupList(){
        return "/view/my_studygroup_list";
    }

    @GetMapping("/my_studygroup")
    public String myStudyGroup(){
        return "/view/my_studygroup";
    }

    @GetMapping("/create_meeting")
    public String createMeeting(){
        return "/view/create_meeting";
    }

    @GetMapping("/my_vocabulary_list")
    public String myVocabularyList(){
        return "/view/my_vocabulary_list";
    }

    @GetMapping("/toeiclab_introduction")
    public String toeiclabIntroduction(){
        return "/view/toeiclab_intro";
    }

    @GetMapping("/select_test")
    public String selectTest(){
        return "/view/select_test";
    }

    @GetMapping("/rc_sheet")
    public String rcSheet(){
        return "/view/rc_sheet";
    }

    @GetMapping("/lc_sheet")
    public String lcSheet(){
        return "/view/lc_sheet";
    }

    @GetMapping("/spk_sheet")
    public String spkSheet(){
        return "/view/spk_sheet";
    }

    @GetMapping("/spk_confirm_answer")
    public String spkConfirmAnswer(){
        return "/view/spk_confirm_answer";
    }

    @GetMapping("/apply_studygroup")
    public String applyStudygroup(){
        return "/view/apply_studygroup";
    }

    @GetMapping("/result_sheet")
    public String resultSheet(){
        return "/view/result_sheet";
    }

    @GetMapping("/my_review_note")
    public String myReviewNote(){
        return "/view/my_review_note";
    }

    @GetMapping("/rc_answer_sheet")
    public String rcAnswerSheet(){
        return "/view/rc_answer_sheet";
    }

    @GetMapping("/lc_answer_sheet")
    public String lcAnswerSheet(){
        return "/view/lc_answer_sheet";
    }

    @GetMapping("/spk_answer_sheet")
    public String spkAnswerSheet(){
        return "/view/spk_answer_sheet";
    }

    @GetMapping("/vocabulary_test")
    public String vocabularyTest(){
        return "/view/vocabulary_test";
    }

    @PostMapping("/popup_dictionary")
    public String popupDictionary(){
        return "/view/popup_dictionary";
    }

    @GetMapping("/forum")
    public String forum(){
        return "/view/forum";
    }

    @GetMapping("/schedule")
    public String schedule(){
        return "/view/schedule";
    }

    @GetMapping("/practice_test/{id}")
    public String practiceTest(@PathVariable String id, Model model){
        System.out.println(id);
        return "/view/practice_test";
    }

    @GetMapping("/test/{type}")
    @Transactional
    public String test1(@CurrentUser Member member, @PathVariable String type, Model model){
        QuestionSet list = new QuestionSet();
        switch (type){
            case "quarter":
//                list = questionSetService.createQuarterToeic();
                break;

            case "half":

                break;


            case "full":

                break;
            default:

                break;
        }
        model.addAttribute("QuestionList", list.getQuestions());
        return "/view/question/test";
    }

}