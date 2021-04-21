package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.service.*;
import toeicLab.toeicLab.user.CurrentUser;
import toeicLab.toeicLab.user.StudyGroupApplicationForm;
import toeicLab.toeicLab.user.StudyGroupApplicationValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StudyGroupController {

    private final MeetingRepository meetingRepository;
    private final CommentRepository commentRepository;
    private final QuestionSetRepository questionSetRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final QuestionSetService questionSetService;
    private final StudyGroupService studyGroupService;
    private final StudyGroupApplicationValidator studyGroupApplicationValidator;
    private final StudyGroupApplicationService studyGroupApplicationService;

    /**
     * [ToeicLab]의 스터디 그룹 신청 페이지로 이동합니다.
     * @param member
     * @param model
     * @return study_group/apply_studygroup
     */
    @GetMapping("/apply_studygroup")
    public String applyStudyGroup(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        model.addAttribute(new StudyGroupApplicationForm());
        return "study_group/apply_studygroup";
    }

    /**
     * 회원이 선택한 신청서를 제출합니다.
     * @param member
     * @param studyGroupApplicationForm
     * @param errors
     * @return study_group/apply_studygroup
     */
    @PostMapping("/apply_studygroup")
    @ResponseBody
    public String SubmitStudyGroupApplication(@CurrentUser Member member, @Valid StudyGroupApplicationForm studyGroupApplicationForm, Errors errors) {

        JsonObject jsonObject = new JsonObject();
        studyGroupApplicationValidator.validate(studyGroupApplicationForm, errors);
        if (errors.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError error : errors.getFieldErrors()){
                sb.append(error.getDefaultMessage() + "<br/>");
            }
            jsonObject.addProperty("message", sb.toString());
            return jsonObject.toString();
        }
        studyGroupApplicationService.createNewStudyGroupApplication(studyGroupApplicationForm, member);
        jsonObject.addProperty("message", "ok");
        return jsonObject.toString();
    }

    /**
     * 회원이 포함된 스터디 그룹으로 이동합니다.
     * @param member
     * @param id
     * @param model
     * @return study_group/my_studygroup_detail
     */
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

        return "study_group/my_studygroup_detail";
    }

    /**
     * 스터디 그룹에서 탈퇴합니다.
     * @param member
     * @param id
     * @return jsonObject
     */
    @GetMapping("/secession_studyGroup")
    @ResponseBody
    @Transactional
    public String secessionStudyGroup(@CurrentUser Member member, @RequestParam("id") Long id){
        JsonObject jsonObject = new JsonObject();

        Member deletedMember = memberRepository.getOne(member.getId());
        StudyGroup studyGroup = studyGroupRepository.getOne(id);

        try {
            if (studyGroup.getMembers().size() == 1){
                studyGroupRepository.delete(studyGroup);

            }
            else {
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

    /**
     * 스터디 그룹 대화창에 등록을 합니다.
     * @param member
     * @param content
     * @param group
     * @return jsonObject
     */
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

    /**
     * 스터디 그룹 이름을 변경합니다.
     * @param name
     * @param group
     * @return jsonObject
     */
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
     * @return study_group/crete_meeting
     */
    @GetMapping("/create_meeting/{id}")
    public String viewCreateMeeting(@CurrentUser Member member, Model model, @PathVariable Long id) {
        model.addAttribute("member", member);
        model.addAttribute("studyGroupId", id);
        return "study_group/create_meeting";
    }
}
