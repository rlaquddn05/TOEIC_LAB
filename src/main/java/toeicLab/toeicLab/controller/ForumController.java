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
public class ForumController {

    @GetMapping("/forum")
    public String forum(Member member, Model model) {

        return "/view/forum";
    }

    @GetMapping("/forum_upload")
    public String uploadQuestion(Member member, Model model){

        return "/view/forum_upload";
    }
}
