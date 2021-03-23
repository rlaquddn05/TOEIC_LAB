package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import toeicLab.toeicLab.domain.MailDto;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.repository.MailRepository;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.service.MailService;
import toeicLab.toeicLab.service.MemberService;
import toeicLab.toeicLab.user.SignUpForm;
import toeicLab.toeicLab.user.SignUpValidator;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final MailService mailService;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;

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
    public String sendResetPassword(String userId, String email, Model model) {
        try{
            memberService.sendResetPasswordEmail(userId, email);
        }catch (IllegalArgumentException e){
            model.addAttribute("error_code", "password.reset.failed");
            return "/view/notify_password";
        }
        model.addAttribute("email", email);
        model.addAttribute("result_code", "password.reset.send");
        return "/view/notify_password";
    }

    @GetMapping("reset_password")
    public String resetPasswordView(String email, String emailCheckToken, Model model){
        boolean result = memberService.checkEmailToken(email, emailCheckToken);

        if(result){
            model.addAttribute("result", true);
        }else{
            model.addAttribute("result", false);
        }

        model.addAttribute("email", email);

        return "/view/reset_password";
    }

    @PostMapping("reset_password")
    public String resetPassword(String email, String password, Model model){
        memberService.resetPassword(email, password);
        model.addAttribute("result_code", "password.reset.success");
        return "/view/notify_password";
    }

    @GetMapping("/send_find_id_link")
    public String sendFindIdView(){
        return "/view/send_find_id_link";
    }

    @PostMapping("/send_find_id_link")
    public String sendFindId(String email, Model model) {
        try{
            memberService.sendFindIdByEmail(email);
        }catch (IllegalArgumentException e){
            model.addAttribute("error_code", "find.id.failed");
            return "/view_find_id";
        }
        model.addAttribute("success_code", "find.id.success");
        return "/view/find_id";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute(new SignUpForm());
        return "/view/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        log.info("하...");
        if(errors.hasErrors()){
            log.info("유효성 에러 발생!");
            return "/view/signup";
        }
        signUpValidator.validate(signUpForm, errors);
        log.info("유효성 검사 끝!");
        memberService.createNewMember(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/email")
    public String sendEmailCheckToken(@RequestParam("email") String email, Model model) {
        MailDto mailDto = new MailDto();
        mailDto.setEmail(email);
        mailDto.setTitle("회원님의 이메일 인증번호입니다.");

        mailService.mailSend(mailDto);
        MailDto checkMailDto = mailRepository.findByEmail(email);
        model.addAttribute("checkMailDto", checkMailDto);
        return "redirect:/view/signup";
    }

    @GetMapping("/checkTokens")
    @ResponseBody
    public String checkTokens(@RequestParam("token") String token, @RequestParam("inputToken") String inputToken){
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = token.equals(inputToken);

        if(result){
            jsonObject.addProperty("message", "이메일 인증 성공");
        }else {
            jsonObject.addProperty("message", "이메일 인증 실패");
        }
        return jsonObject.toString();
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public String checkUserId(@RequestParam("userId") String userId){
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = memberRepository.existsByUserId(userId);

        if(result){
            jsonObject.addProperty("message", "이미 존재하는 아이디입니다.");
        }else {
            jsonObject.addProperty("message", "사용 가능한 아이디입니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/checkNickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname){
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = memberRepository.existsByNickname(nickname);

        if(result){
            jsonObject.addProperty("message", "이미 존재하는 닉네임입니다.");
        }else {
            jsonObject.addProperty("message", "사용 가능한 닉네임입니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/checkPasswords")
    @ResponseBody
    public String checkPasswords(@RequestParam("password") String password, @RequestParam("check_password") String check_password){
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = password.equals(check_password);

        if(result){
            jsonObject.addProperty("message", "비밀번호가 일치합니다,");
        }else {
            jsonObject.addProperty("message", "비밀번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
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

    @GetMapping("/practice_test")
    public String practiceTest(){
        return "/view/practice_test";
    }

}