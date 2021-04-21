package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.service.*;
import toeicLab.toeicLab.user.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final StudyGroupRepository studyGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final MailService mailService;

    /**
     * [ToeicLab]의 기본페이지 입니다.
     * @param member
     * @param model
     * @return member/index
     */
    @GetMapping("/")
    public String index(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        return "member/index";
    }

    @GetMapping("/index")
    public String indexView(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        return "member/index";
    }

    /**
     * [ToeicLab]의 로그인페이지 입니다.
     * @return member/login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "member/login";
    }

    /**
     * [ToeicLab]의 로그아웃했을 경우 돌아가는 기본페이지입니다.
     * @return member/index
     */
    @GetMapping("/logout")
    public String showLogoutPage() {
        return "member/index";
    }

    /**
     * [ToeicLab]의 비밀번호 초기화페이지입니다.
     * @return member/send_reset_password_link
     */
    @GetMapping("/send_reset_password_link")
    public String sendResetPasswordView() {
        return "member/send_reset_password_link";
    }

    /**
     * 회원의 email로 전달된 token값을 가지고 올바른 token값인지 확인 후에 재설정 페이지로 넘어갑니다.
     * @param userId
     * @param email
     * @param model
     * @return member/notify_password
     */
    @PostMapping("/send_reset_password_link")
    public String sendResetPassword(@RequestParam("userId") String userId, @RequestParam("email") String email, Model model) {
        try {
            Member checkedMember = memberService.sendResetPasswordEmail(userId, email);
            model.addAttribute("checkedMember", checkedMember);
            MailDto mailDto = new MailDto();
            mailDto.setEmail(checkedMember.getEmail());
            mailDto.setEmailCheckToken(mailDto.generateEmailCheckToken());

            mailService.resetPasswordMailSend(mailDto);
            MailDto existedMail = mailRepository.findByEmail(email);
            existedMail.setEmailCheckToken(mailDto.getEmailCheckToken());
            mailRepository.save(existedMail);

            model.addAttribute("resetPasswordEmail", mailDto);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "password.reset.failed");
            return "member/notify_password";
        }
        model.addAttribute("result_code", "password.reset.send");
        return "member/notify_password";
    }

    /**
     * 비밀번호 초기화 확인여부 페이지입니다.
     * @return member/notify_password
     */
    @GetMapping("/notify_password")
    public String notifyPasswordView() {
        return "member/notify_password";
    }

    /**
     * 재설정 token 확인 후에 재설정 페이지로 넘어갑니다.
     * @param resetPasswordEmailToken
     * @param model
     * @return member/reset_password
     */
    @PostMapping("/notify_password")
    public String goResetPassword(@RequestParam("token") String resetPasswordEmailToken, Model model) {
        MailDto mailByEmailCheckToken = mailRepository.findByEmailCheckToken(resetPasswordEmailToken);
        model.addAttribute("email", mailByEmailCheckToken.getEmail());
        return "member/reset_password";
    }

    /**
     * 비밀번호 초기화 시 이메일로 전송된 token과 생성 된 token을 확인합니다.
     * @param resetPasswordEmail
     * @param resetPasswordEmailToken
     * @return jsonObject
     */
    @GetMapping("/reset/checkTokens")
    @ResponseBody
    public String resetCheckTokens(@RequestParam("resetPasswordEmail") String resetPasswordEmail,
                                   @RequestParam("token") String resetPasswordEmailToken) {
        JsonObject jsonObject = new JsonObject();
        MailDto getResetPasswordTokenMail = mailRepository.findByEmail(resetPasswordEmail);
        boolean result = false;

        result = getResetPasswordTokenMail.getEmailCheckToken().equals(resetPasswordEmailToken);

        if (result) {
            jsonObject.addProperty("message", "비밀번호 초기화 인증 성공");
        } else {
            jsonObject.addProperty("message", "비밀번호 초기화 인증번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 비밀번호 초기화 페이지로 이동합니다.
     * @return member/reset_password
     */
    @GetMapping("reset_password")
    public String resetPasswordView() {
        return "member/reset_password";
    }

    /**
     * 새로 입력받은 비밀번호를 저장합니다.
     * @param email
     * @param password
     * @param model
     * @return member/notify_password
     */
    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
        memberService.resetPassword(email, password);
        model.addAttribute("result_code", "password.reset.success");
        return "member/notify_password";
    }

    /**
     * 아이디 찾기 페이지로 이동합니다.
     * @return member/send_find_id_link
     */
    @GetMapping("/send_find_id_link")
    public String sendFindIdView() {
        return "member/send_find_id_link";
    }

    /**
     * 입력받은 이메일로 조회하여 일치하는 아이디를 찾아냅니다.
     * @param email
     * @param model
     * @return member/find_id
     */
    @PostMapping("/send_find_id_link")
    public String sendFindId(String email, Model model) {
        try {
            Member findIdMember = memberService.sendFindIdByEmail(email);
            model.addAttribute("findIdMember", findIdMember);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "find.id.failed");
            return "member/find_id";
        }
        model.addAttribute("success_code", "find.id.success");
        return "member/find_id";
    }

    /**
     * [ToeicLab]의 회원가입페이지로 이동합니다.
     * @param model
     * @return member/signup
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute(new SignUpForm());
        return "member/signup";
    }

    /**
     * 회원가입 시 입력받은 정보들의 유효성검사를 한 뒤에 회원정보에 저장합니다.
     * @param signUpForm
     * @param errors
     * @return redirect:/index
     */
    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "member/signup";
        }
        signUpValidator.validate(signUpForm, errors);

        Member member = memberService.createNewMember(signUpForm);

        memberService.autologin(member);

        return "redirect:/index";
    }

    /**
     * 사용자가 입력한 이메일로 token을 전송합니다.
     * @param email
     * @return jsonObject
     */
    @GetMapping("/signup/email")
    @ResponseBody
    public String sendEmailCheckToken(@RequestParam("email") String email) {

        JsonObject jsonObject = new JsonObject();
        boolean result = false;

        MailDto checkEmail = mailRepository.findByEmail(email);
        if(checkEmail==null){
            MailDto mailDto = new MailDto();
            mailDto.setEmail(email);
            mailDto.setEmailCheckToken(mailDto.generateEmailCheckToken());
            MailDto saveMail = mailService.mailSend(mailDto);
            mailRepository.save(saveMail);
            result = saveMail != null;
        }else{
            result = false;
        }

        if (result) {
            jsonObject.addProperty("message", "이메일을 확인하세요.");
        } else {
            jsonObject.addProperty("message", "이미 존재하는 이메일입니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 이메일로 전송받은 token값과 기존의 token값을 비교하여 이메일 인증절차를 진행합니다.
     * @param email
     * @param certification_number
     * @return jsonObject
     */
    @GetMapping("/signup/checkTokens")
    @ResponseBody
    public String checkTokens(@RequestParam("email") String email, @RequestParam("certification_number") String certification_number) {

        JsonObject jsonObject = new JsonObject();
        MailDto getTokenMail = mailRepository.findByEmail(email);
        boolean result = false;

        result = getTokenMail.getEmailCheckToken().equals(certification_number);

        if (result) {
            jsonObject.addProperty("message", "이메일 인증 성공.");
        } else {
            jsonObject.addProperty("message", "이메일 인증번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 사용자가 입력한 아이디의 사용여부를 판단합니다.
     * @param userId
     * @return jsonObject
     */
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

    /**
     * 사용자가 입력한 닉네임의 사용여부를 판단합니다.
     * @param nickname
     * @return jsonObject
     */
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

    /**
     * 입력한 비밀번호와 재입력한 비밀번호의 일치여부를 판단합니다.
     * @param password
     * @param check_password
     * @return jsonObject
     */
    @GetMapping("/signup/checkPasswords")
    @ResponseBody
    public String checkPasswords(@RequestParam("password") String password, @RequestParam("check_password") String check_password) {
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        result = password.equals(check_password);

        if (result) {
            jsonObject.addProperty("message", "비밀번호가 일치합니다.");
        } else {
            jsonObject.addProperty("message", "비밀번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 기존의 비밀번호 위에 새로운 비밀번호를 저장하여 수정합니다.
     * @param password
     * @param userId
     * @return jsonObject
     */
    @GetMapping("/update/password")
    @ResponseBody
    public String updatePassword(@RequestParam("password") String password, @RequestParam("userId") String userId) {

        Member member = memberRepository.findByUserId(userId);
        JsonObject jsonObject = new JsonObject();

        if (member.getPassword() == null) {
            member.setPassword("{noop}" + password);
            member.encodePassword(passwordEncoder);
            memberRepository.save(member);
            jsonObject.addProperty("message", "비밀번호를 등록하였습니다.");
        } else {
            member.setPassword("{noop}" + password);
            memberRepository.save(member);
            jsonObject.addProperty("message", "비밀번호를 수정하였습니다.");
        }
        return jsonObject.toString();
    }


    /**
     * 현재 로그인한 사용자의 정보를 조회하여 마이페이지에 불러옵니다.
     * @param member
     * @param model
     * @return member/my_page
     */
    @GetMapping("/my_page")
    public String myPageView(@CurrentUser Member member, Model model) {
        if (member.getGenderType() == null){
            member.setGenderType(GenderType.MALE);
            memberRepository.save(member);
        }
        Member currentUser = memberRepository.findByEmail(member.getEmail());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("member", member);
        model.addAttribute(new UpdateForm());
        return "member/my_page";
    }

    /**
     * 기존의 회원정보에서 수정된 사항으로 회원정보를 수정합니다.
     * @param updateForm
     * @return redirect:/my_page
     */
    @PostMapping("/my_page")
    @ResponseBody
    public String myPageSubmit(UpdateForm updateForm) {
        JsonObject jsonObject = new JsonObject();
        Member updatedMember = memberRepository.findByUserId(updateForm.getUserId());
        if (updateForm.getNickname().equals("")){
            jsonObject.addProperty("message", "닉네임은 공란으로 설정할 수 없습니다.");
            return jsonObject.toString();
        }
        if (memberRepository.existsByNickname(updateForm.getNickname())){
            jsonObject.addProperty("message", "중복된 닉네임으로 변경할 수 없습니다.");
            return jsonObject.toString();
        }
        if(updateForm.getAge() == null ){
            jsonObject.addProperty("message", "나이를 입력해주세요");
            return jsonObject.toString();
        }
        if(updateForm.getAge() < 1 ){
            jsonObject.addProperty("message", "올바른 나이를 입력해주세요");
            return jsonObject.toString();
        }
        if (updateForm.getContact().equals("")){
            jsonObject.addProperty("message", "전화번호를 입력해주세요");
            return jsonObject.toString();
        }
        try {
            updatedMember.setAge(updateForm.getAge());
            updatedMember.setContact(updateForm.getContact());
            updatedMember.setNickname(updateForm.getNickname());
            updatedMember.setAddress(Address.builder()
                    .zipcode(updateForm.getZipcode())
                    .city(updateForm.getCity())
                    .street(updateForm.getStreet())
                    .build());
        }catch (Exception e){
            jsonObject.addProperty("message", "예상치 못한 오류가 발생하였습니다.");
            return jsonObject.toString();
        }
        if (!updatedMember.getStudyGroupList().isEmpty()){
            List<StudyGroup> studyGroupList = updatedMember.getStudyGroupList();
            for (StudyGroup sg : studyGroupList){
                List<Comment> commentList = sg.getComments();
                for (Comment comment : commentList){
                    if (comment.getWriterId().equals(updatedMember.getId())) {
                        comment.setWriterNick(updatedMember.getNickname());
                        commentRepository.save(comment);
                    }
                }
                studyGroupRepository.save(sg);
            }
        }
        if (updateForm.getGender().equals("male")) {
            updatedMember.setGenderType(GenderType.MALE);
        } else if (updateForm.getGender().equals("female")) {
            updatedMember.setGenderType(GenderType.FEMALE);
        } else {
            updatedMember.setGenderType(updatedMember.getGenderType());
        }
        memberRepository.save(updatedMember);
        jsonObject.addProperty("message", "정보수정 완료");
        return jsonObject.toString();
    }

    /**
     * 사용자의 아이디를 조회하여 회원정보를 삭제합니다(탈퇴).
     * @param userId
     * @param request
     * @return jsonObject
     */
    @GetMapping("/update/delete")
    @ResponseBody
    public String deletePassword(@RequestParam("userId") String userId, HttpServletRequest request) {

        Member deletedMember = memberRepository.findByUserId(userId);
        List<StudyGroup> sg = deletedMember.getStudyGroupList();

        for (StudyGroup studyGroup : sg) {
            for (int j = 0; j < studyGroup.getMembers().size(); ++j) {
                if (studyGroup.getMembers().size() == 1) {
                    studyGroupRepository.deleteById(studyGroup.getId());
                    break;
                }
                if (studyGroup.getReaderId().equals(deletedMember.getId()) && studyGroup.getMembers().get(j).getId().equals(deletedMember.getId())) {
                    studyGroup.setReaderId(studyGroup.getMembers().get(j + 1).getId());
                    studyGroupRepository.save(studyGroup);
                }
            }
        }

        for (StudyGroup s : sg){
            if (s.getMembers().size() > 1){
                for (Meeting meeting : s.getMeetings()) {
                    meeting.getQuestionSets().removeIf(qs -> qs.getMember().getId().equals(deletedMember.getId()));
                }
                s.getMembers().remove(deletedMember);
                studyGroupRepository.save(s);
            }

        }

        memberRepository.deleteById(deletedMember.getId());

        boolean result = true;
        result = memberRepository.existsById(deletedMember.getId());
        JsonObject jsonObject = new JsonObject();

        if (result) {
            jsonObject.addProperty("message", "[ToeicLab]을 탈퇴실패...");

        } else {
            jsonObject.addProperty("message", "[ToeicLab]을 탈퇴했습니다.");
            HttpSession session= request.getSession(false);
            SecurityContextHolder.clearContext();
            if(session != null) {
                session.invalidate();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 사용자가 변경하고자하는 닉네임의 존재 유무를 확인합니다.
     * @param nickname
     * @return jsonObject
     */
    @GetMapping("/update/checkNickname")
    @ResponseBody
    public String checkNicknameAgain(@RequestParam("nickname") String nickname) {
        JsonObject jsonObject = new JsonObject();

        boolean result = false;

        if (nickname.equals("")){
            jsonObject.addProperty("message", "닉네임은 공란으로 설정할 수 없습니다.");
            return jsonObject.toString();
        }

        result = memberRepository.existsByNickname(nickname);
        if (result) {
            jsonObject.addProperty("message", "이미 존재하는 닉네임입니다.");
        } else {
            jsonObject.addProperty("message", "사용 가능한 닉네임입니다.");
        }
        return jsonObject.toString();
    }

}