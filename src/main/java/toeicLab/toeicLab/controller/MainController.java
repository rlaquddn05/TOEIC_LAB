package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
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

    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final MailService mailService;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final QuestionSetService questionSetService;
    private final QuestionSetRepository questionSetRepository;
    private final StudyGroupApplicationValidator studyGroupApplicationValidator;
    private final StudyGroupApplicationService studyGroupApplicationService;
    private final StudyGroupRepository studyGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudyGroupService studyGroupService;
    private final MeetingRepository meetingRepository;
    private final CommentRepository commentRepository;

    /**
     * [ToeicLab]의 기본페이지 입니다.
     * @param member
     * @param model
     * @return view/index
     */
    @GetMapping("/")
    public String index(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        return "view/index";
    }

    @GetMapping("/index")
    public String indexView(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        return "view/index";
    }

    /**
     * [ToeicLab]의 로그인페이지 입니다.
     * @return view/login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "view/login";
    }

    /**
     * [ToeicLab]의 로그아웃했을 경우 돌아가는 기본페이지입니다.
     * @return view/index
     */
    @GetMapping("/logout")
    public String showLogoutPage() {
        return "view/index";
    }

    /**
     * [ToeicLab]의 비밀번호 초기화페이지입니다.
     * @return view/send_reset_password_link
     */
    @GetMapping("/send_reset_password_link")
    public String sendResetPasswordView() {
        return "view/send_reset_password_link";
    }

    /**
     * 회원의 email로 전달된 token값을 가지고 올바른 token값인지 확인 후에 재설정 페이지로 넘어갑니다.
     * @param userId
     * @param email
     * @param model
     * @return view/notify_password
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
            return "view/notify_password";
        }
        model.addAttribute("result_code", "password.reset.send");
        return "view/notify_password";
    }

    /**
     * 비밀번호 초기화 확인여부 페이지입니다.
     * @return view/notify_password
     */
    @GetMapping("/notify_password")
    public String notifyPasswordView() {
        return "view/notify_password";
    }

    /**
     * 재설정 token 확인 후에 재설정 페이지로 넘어갑니다.
     * @param resetPasswordEmailToken
     * @param model
     * @return view/reset_password
     */
    @PostMapping("/notify_password")
    public String goResetPassword(@RequestParam("token") String resetPasswordEmailToken, Model model) {
        MailDto mailByEmailCheckToken = mailRepository.findByEmailCheckToken(resetPasswordEmailToken);
        model.addAttribute("email", mailByEmailCheckToken.getEmail());
        return "view/reset_password";
    }

    /**
     * 비밀번호 초기화 시 이메일로 전송된 token과 생성 된 token을 확인합니다.
     * @param resetPasswordEmail
     * @param resetPasswordEmailToken
     * @return
     */
    @GetMapping("/reset/checkTokens")
    @ResponseBody
    public String resetCheckTokens(@RequestParam("resetPasswordEmail") String resetPasswordEmail,
                                   @RequestParam("token") String resetPasswordEmailToken) {
        log.info("비밀번호 초기화 인증키 판단");
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

    /**
     * 비밀번호 초기화 페이지로 이동합니다.
     * @return view/reset_password
     */
    @GetMapping("reset_password")
    public String resetPasswordView() {
        return "view/reset_password";
    }

    /**
     * 새로 입력받은 비밀번호를 저장합니다.
     * @param email
     * @param password
     * @param model
     * @return view/notify_password
     */
    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
        memberService.resetPassword(email, password);
        log.info("비밀번호 수정 완료");
        model.addAttribute("result_code", "password.reset.success");
        return "view/notify_password";
    }

    /**
     * 아이디 찾기 페이지로 이동합니다.
     * @return
     */
    @GetMapping("/send_find_id_link")
    public String sendFindIdView() {
        return "view/send_find_id_link";
    }

    /**
     * 입력받은 이메일로 조회하여 일치하는 아이디를 찾아냅니다.
     * @param email
     * @param model
     * @return view/find_id
     */
    @PostMapping("/send_find_id_link")
    public String sendFindId(String email, Model model) {
        try {
            Member findIdMember = memberService.sendFindIdByEmail(email);
            model.addAttribute("findIdMember", findIdMember);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "find.id.failed");
            return "view/find_id";
        }
        model.addAttribute("success_code", "find.id.success");
        return "view/find_id";
    }

    /**
     * [ToeicLab]의 회원가입페이지로 이동합니다.
     * @param model
     * @return view/signup
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute(new SignUpForm());
        return "/view/signup";
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
            log.info("유효성 에러 발생!");
            return "view/signup";
        }
        signUpValidator.validate(signUpForm, errors);
        log.info("유효성 검사 끝!");

        Member member = memberService.createNewMember(signUpForm);

        memberService.autologin(member); // 해당 멤버를 자동 로그인 해주기

        return "redirect:/index";
    }

    /**
     * 사용자가 입력한 이메일로 token을 전송합니다.
     * @param email
     * @return
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
     * @return
     */
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
            jsonObject.addProperty("message", "이메일 인증 성공.");
        } else {
            jsonObject.addProperty("message", "이메일 인증번호가 일치하지 않습니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 사용자가 입력한 아이디의 사용여부를 판단합니다.
     * @param userId
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     * @return view/my_page
     */
    @GetMapping("/my_page")
    public String myPageView(@CurrentUser Member member, Model model) {
        if (member.getGenderType() == null){
            member.setGenderType(GenderType.MALE);
            memberRepository.save(member);
        }
        Member currentUser = memberRepository.findByEmail(member.getEmail());
        model.addAttribute("currentUser", currentUser);
        log.info(String.valueOf(currentUser.getGenderType()));
        model.addAttribute("member", member);
        model.addAttribute(new UpdateForm());
        return "view/my_page";
    }

    /**
     * 기존의 회원정보에서 수정된 사항으로 회원정보를 수정합니다.
     * @param updateForm
     * @return redirect:/my_page
     */
    @PostMapping("/my_page")
    public String myPageSubmit(UpdateForm updateForm) {
        Member updatedMember = memberRepository.findByUserId(updateForm.getUserId());
        updatedMember.setAge(updateForm.getAge());
        updatedMember.setContact(updateForm.getContact());
        updatedMember.setNickname(updateForm.getNickname());
        updatedMember.setAddress(Address.builder()
                .zipcode(updateForm.getZipcode())
                .city(updateForm.getCity())
                .street(updateForm.getStreet())
                .build());
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
        return "redirect:/my_page";
    }

    /**
     * 사용자의 아이디를 조회하여 회원정보를 삭제합니다(탈퇴).
     * @param userId
     * @param request
     * @return
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


    @GetMapping("/my_studygroup_detail/{id}")
    public String myStudyGroupDetail(@CurrentUser Member member, @PathVariable String id, Model model) {
        Long longId = Long.parseLong(id);
        StudyGroup thisStudyGroup = studyGroupRepository.getOne(longId);
        List<Meeting> meetings = thisStudyGroup.getMeetings();
        List<QuestionSet> questionSets = new ArrayList<>();
        Map<Long, String> comment = new HashMap<>();
        Map<Long, QuestionSet> map = new HashMap<>();
        if (meetings != null) {
            for (Meeting m : meetings){
                for(QuestionSet qs : m.getQuestionSets()){
                    if (qs.getMember().getId().equals(member.getId())){
                        questionSets.add(qs);
                        map.put(m.getId(), qs);
                        if (!qs.getSubmittedAnswers().isEmpty()){
                            comment.put(qs.getId(), memberService.CreateProgressByQuestionSet(qs));
                        }
                        else {
                            comment.put(qs.getId(), null);
                        }
                    }
                }
            }
            model.addAttribute("questionSets", questionSets);
            model.addAttribute("comment", comment);
        }
        List<Member> memberList = thisStudyGroup.getMembers();
        for(Member m : memberList) {
            if (m.getId().toString().equals(thisStudyGroup.getReaderId().toString())){
                model.addAttribute("leader", m);
                memberList.remove(m);
                break;
            }
        }

        model.addAttribute("memberList", memberList);
        model.addAttribute("member", member);
        model.addAttribute("studyGroupId", Long.parseLong(id));
        model.addAttribute("thisStudyGroup", thisStudyGroup);
        model.addAttribute("meetings", meetings);
        model.addAttribute("map",map);

        List<Comment> commentList = thisStudyGroup.getComments();


        model.addAttribute("commentList", commentList);



        return "view/my_studygroup_detail";
    }

//    @PostMapping("/upComment")
//    public String uploadCommentGroup(@CurrentUser Member member, @RequestParam("comment") String comment, @RequestParam("studyGroupId") long id, Model model){
//        StudyGroup studyGroup = studyGroupRepository.getOne(id);
//        String message = member.getNickname() + ":: " + comment;
//        List<String> cm = studyGroup.getContent();
//        cm.add(message);
//        studyGroup.setContent(cm);
//        studyGroupRepository.save(studyGroup);
//        model.addAttribute(member);
//        return "redirect:/my_studygroup_detail/"+id;
//    }

    @GetMapping("/secession_studyGroup")
    @ResponseBody
    @Transactional
    public String secessionStudyGroup(@CurrentUser Member member, @RequestParam("id") Long id){
        JsonObject jsonObject = new JsonObject();

        Member deletedMember = memberRepository.getOne(member.getId());
        StudyGroup studyGroup = studyGroupRepository.getOne(id);
        System.out.println(studyGroup.getId());

        try {
//            studyGroupService.signOutStudyGroup(member, studyGroup);


            if (studyGroup.getMembers().size() == 1){
                studyGroupRepository.delete(studyGroup);

            }
            else {
                System.out.println("1");
                if (!studyGroup.getMeetings().isEmpty()){
                    for (Meeting meeting : studyGroup.getMeetings()) {


                        List<QuestionSet> targetQuestionSets = new ArrayList<>();

                        for(QuestionSet questionSet : meeting.getQuestionSets()) {
                            if(questionSet.getMember().equals(deletedMember)){
                                targetQuestionSets.add(questionSet);
                            }
                        }
                        for (QuestionSet questionSet : targetQuestionSets){
                            meeting.getQuestionSets().remove(questionSet);
                            questionSetRepository.delete(questionSet);
                        }

                        meetingRepository.save(meeting);
                    }
                }
                List<Member> members = studyGroup.getMembers();
                members.remove(deletedMember);
                if (studyGroup.getReaderId().equals(deletedMember.getId())) {
                    studyGroup.setReaderId(members.get(0).getId());
                }

            }
            deletedMember.getStudyGroupList().remove(studyGroup);


            jsonObject.addProperty("message", "스터디그룹을 탈퇴했습니다.");
        } catch (Exception e){
            System.out.println(e.toString());
            jsonObject.addProperty("message", "오류가 발생하였습니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/add_comment")
    @ResponseBody
    @Transactional
    public String addComment(@CurrentUser Member member, @RequestParam("content") String content, @RequestParam("group") Long group){
        JsonObject jsonObject = new JsonObject();
        Member thisMember = memberRepository.getOne(member.getId());
        if (content.equals("")){
            jsonObject.addProperty("message", "공란은 추가할 수 없습니다.");
            return jsonObject.toString();
        }
        try {
            StudyGroup studyGroup = studyGroupRepository.getOne(group);
            Comment comment = new Comment();
            comment.setWriterId(thisMember.getId());
            comment.setWriterNick(thisMember.getNickname());
            comment.setContent(content);
            commentRepository.save(comment);
            studyGroup.getComments().add(comment);
            studyGroupRepository.save(studyGroup);
            jsonObject.addProperty("message", content + group);
        } catch (Exception e){
            System.out.println(e.toString());
            jsonObject.addProperty("message", "오류가 발생하였습니다.");
        }

        return jsonObject.toString();
    }

    @GetMapping("/modify_study_name")
    @ResponseBody
    public String modifyStudyName(@RequestParam("name") String name, @RequestParam("group") Long group){
        JsonObject jsonObject = new JsonObject();
        if (name.equals("")){
            jsonObject.addProperty("message", "공란으로 변경할 수 없습니다.");
            return jsonObject.toString();
        }
        try {
            studyGroupService.changeName(group, name);
            jsonObject.addProperty("message", "스터디 이름이 변경되었습니다.");
        } catch (Exception e){
            jsonObject.addProperty("message", "오류가 발생하였습니다.");
        }
        return jsonObject.toString();
    }

    @GetMapping("/modify_study_leader")
    @ResponseBody
    public String modifyStudyLeader(@RequestParam("target") Long target, @RequestParam("group") Long group){
        JsonObject jsonObject = new JsonObject();
        try {
            studyGroupService.changeLeader(group, target);
            jsonObject.addProperty("message", "조장이 변경되었습니다.");
        } catch (Exception e){
            jsonObject.addProperty("message", "오류가 발생하였습니다.");
        }
        return jsonObject.toString();
    }

    /**
     * 사용자가 매칭된 여러 스터디 그룹중 본인이 선택한 스터디그룹에 대해 확인합니다.
     * @param member
     * @param id
     * @param model
     * @param date
     * @param select_form
     * @return redirect:/my_studygroup_detail/{id}
     */
    @PostMapping("/create_meeting/{id}")
    public String myStudyGroupDetailPost(@CurrentUser Member member, @PathVariable String id, Model model, String date,
                                         String[] select_form) {

        int[] numberOfQuestions = questionSetService.selectFormToArray(select_form);

        StudyGroup thisStudyGroup = studyGroupRepository.findById(Long.parseLong(id));
        model.addAttribute("questionSetId", id);
        model.addAttribute("member", member);
        questionSetService.createMeeting(thisStudyGroup, numberOfQuestions, date);
        return "redirect:/my_studygroup_detail/{id}";
    }

    /**
     * [ToeicLab]스터디생성 페이지로 이동합니다.
     * @param member
     * @param model
     * @param id
     * @return view/crete_meeting
     */
    @GetMapping("/create_meeting/{id}")
    public String viewCreateMeeting(@CurrentUser Member member, Model model, @PathVariable Long id) {
        model.addAttribute("member", member);
        model.addAttribute("studyGroupId", id);
        return "view/create_meeting";
    }

    /**
     * [ToeicLab]의 소개페이지로 이동합니다.
     * @return view/toeiclab_intro
     */
    @GetMapping("/toeiclab_introduction")
    public String toeiclabIntroduction() {
        return "view/toeiclab_intro";
    }

    /**
     * [ToeicLab]의 모의고사 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/select_test
     */
    @GetMapping("/select_test")
    public String selectTest(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/select_test";
    }

    /**
     * [ToeicLab]의 RC문제풀이 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/re_sheet
     */
    @GetMapping("/rc_sheet")
    public String rcSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/rc_sheet";
    }

    /**
     * [ToeicLab]의 LC문제풀이 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/practice_sheet
     */
    @GetMapping("/lc_sheet")
    public String lcSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/practice_sheet";
    }

    /**
     * [ToeicLab]의 SPK문제풀이 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/spk_sheet
     */
    @GetMapping("/spk_sheet")
    public String spkSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/spk_sheet";
    }

    /**
     * [ToeicLab]의 SPK문제확인 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/spk_confirm_answer
     */
    @GetMapping("/spk_confirm_answer")
    public String spkConfirmAnswer(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/spk_confirm_answer";
    }

    /**
     * [ToeicLab]의 스터디 그룹 신청 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/apply_studygroup
     */
    @GetMapping("/apply_studygroup")
    public String applyStudyGroup(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        model.addAttribute(new StudyGroupApplicationForm());
        return "view/apply_studygroup";
    }

    /**
     * 회원이 선택한 신청서를 제출합니다.
     * @param model
     * @param member
     * @param studyGroupApplicationForm
     * @param errors
     * @return view/apply_studygroup
     */
    @PostMapping("/apply_studygroup")
    public String SubmitStudyGroupApplication(Model model, @CurrentUser Member member, @Valid StudyGroupApplicationForm studyGroupApplicationForm, Errors errors) {
        model.addAttribute("member", member);
        studyGroupApplicationValidator.validate(studyGroupApplicationForm, errors);
        if (errors.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError error : errors.getFieldErrors()){
                sb.append(error.getDefaultMessage() + "<br/>");
            }
            model.addAttribute("errorMessage", sb);
            return "view/apply_studygroup";
        }
        studyGroupApplicationService.createNewStudyGroupApplication(studyGroupApplicationForm, member);
        model.addAttribute("successMessage", "스터디 신청이 완료되었습니다.");
        return "view/apply_studygroup";
    }

    /**
     * RC정답 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/rc_answer_sheet
     */
    @GetMapping("/rc_answer_sheet")
    public String rcAnswerSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/rc_answer_sheet";
    }

    /**
     * LC정답 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/detail
     */
    @GetMapping("/lc_answer_sheet")
    public String lcAnswerSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/detail";
    }

    /**
     * SPK정답 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/spk_answer_sheet
     */
    @GetMapping("/spk_answer_sheet")
    public String spkAnswerSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/spk_answer_sheet";
    }

    /**
     * 일정 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/schedule
     */
    @GetMapping("/schedule")
    public String schedule(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "view/schedule";
    }

    /**
     * 사용자가 문제풀이를 한 뒤에 자신의 학습현황 페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/my_progress
     */
    @GetMapping("/my_progress")
    public String myProgress(@CurrentUser Member member, Model model) {
        QuestionSet qToeic = null;
        QuestionSet hToeic = null;
        QuestionSet fToeic = null;
        QuestionSet pToeic = null;
        member = memberRepository.getOne(member.getId());
        if (member.getQuestionSetList() != null) {
            List<QuestionSet> questionSetList = questionSetRepository.getAllByMember(member);
            for (QuestionSet qs : questionSetList) {
                if (qs.getQuestionSetType().toString() == "QUARTER_TOEIC") qToeic = qs;
                else if (qs.getQuestionSetType().toString() == "HALF_TOEIC") hToeic = qs;
                else if (qs.getQuestionSetType().toString() == "FULL_TOEIC") fToeic = qs;
                else if (qs.getQuestionSetType().toString() == "PRACTICE") pToeic = qs;
                else continue;
            }
        }
        if (qToeic != null) {
            model.addAttribute("qToeic", qToeic);
            model.addAttribute("qToeicComment", memberService.CreateProgressByQuestionSet(qToeic));
        }
        if (hToeic != null) {
            model.addAttribute("hToeic", hToeic);
            model.addAttribute("hToeicComment", memberService.CreateProgressByQuestionSet(hToeic));
        }
        if (fToeic != null) {
            model.addAttribute("fToeic", fToeic);
            model.addAttribute("fToeicComment", memberService.CreateProgressByQuestionSet(fToeic));
        }
        if (pToeic != null) {
            model.addAttribute("pToeic", pToeic);
            model.addAttribute("pToeicComment", memberService.CreateProgressByQuestionSet(pToeic));
        }
        model.addAttribute("part1", memberService.createCommentByQuestionType(member, QuestionType.PART1));
        model.addAttribute("part2", memberService.createCommentByQuestionType(member, QuestionType.PART2));
        model.addAttribute("part3", memberService.createCommentByQuestionType(member, QuestionType.PART3));
        model.addAttribute("part4", memberService.createCommentByQuestionType(member, QuestionType.PART4));
        model.addAttribute("part5", memberService.createCommentByQuestionType(member, QuestionType.PART5));
        model.addAttribute("part6", memberService.createCommentByQuestionType(member, QuestionType.PART6));
        model.addAttribute("part7s", memberService.createCommentByQuestionType(member, QuestionType.PART7_SINGLE_PARAGRAPH));
        model.addAttribute("part7m", memberService.createCommentByQuestionType(member, QuestionType.PART7_MULTIPLE_PARAGRAPH));
        model.addAttribute("level", member.getLevelType() == null ? "데이터가 없습니다" : member.getLevelType());
        model.addAttribute("member", member);
        return "/view/my_progress";
    }

    @GetMapping("/update/checkNickname")
    @ResponseBody
    public String checkNicknameAgain(@RequestParam("nickname") String nickname) {
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
}