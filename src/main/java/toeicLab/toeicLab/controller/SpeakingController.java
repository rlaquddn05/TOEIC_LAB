package toeicLab.toeicLab.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.user.CurrentUser;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SpeakingController {

    /**
     * [ToeicLab]의 SPK문제풀이 페이지로 이동합니다.
     * @param member
     * @param model
     * @return speaking/spk_sheet
     */
    @GetMapping("/spk_sheet")
    public String spkSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "speaking/spk_sheet";
    }

    /**
     * [ToeicLab]의 SPK문제확인 페이지로 이동합니다.
     * @param member
     * @param model
     * @return speaking/spk_confirm_answer
     */
    @GetMapping("/spk_confirm_answer")
    public String spkConfirmAnswer(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "speaking/spk_confirm_answer";
    }

    /**
     * SPK정답 페이지로 이동합니다.
     * @param member
     * @param model
     * @return speaking/spk_answer_sheet
     */
    @GetMapping("/spk_answer_sheet")
    public String spkAnswerSheet(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "speaking/spk_answer_sheet";
    }

    /**
     * 일정 페이지로 이동합니다.
     * @param member
     * @param model
     * @return speaking/schedule
     */
    @GetMapping("/schedule")
    public String schedule(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "speaking/schedule";
    }

    /**
     * [ToeicLab]의 소개페이지로 이동합니다.
     * @return speaking/toeiclab_intro
     */
    @GetMapping("/toeiclab_introduction")
    public String toeiclabIntroduction() {
        return "speaking/toeiclab_intro";
    }
}
