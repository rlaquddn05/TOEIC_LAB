package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.Bulletin;
import toeicLab.toeicLab.domain.BulletinComment;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.repository.BulletinCommentRepository;
import toeicLab.toeicLab.repository.BulletinRepository;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.user.CurrentUser;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinRepository bulletinRepository;
    private final BulletinCommentRepository bulletinCommentRepository;

    @GetMapping("/bulletin")
    public String bulletin(Member member, Model model) {

        List<Bulletin> bulletinList = bulletinRepository.findAll();
        model.addAttribute("bulletinList", bulletinList);

        model.addAttribute(member);
        return "/view/bulletin";
    }

    @GetMapping("/bulletin_upload")
    public String showBulletinUploadView(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);
        model.addAttribute(member);
        return "/view/bulletin_upload";
    }

    @PostMapping("/bulletin_upload")
    public String uploadBulletin(Member member , Model model, String title, String content, String memberId){
        log.info("글쓰기");
        log.info(String.valueOf(memberId));

        Bulletin bulletin = Bulletin.builder()
                .title(title)
                .content(content)
                .writerId(memberId)
                .date(LocalDateTime.now())
                .hit(0L)
                .likeNumber(0L)
                .build();
        model.addAttribute(member);
        bulletinRepository.save(bulletin);
        return "redirect:/bulletin";
    }

    @GetMapping("/bulletinDetail/{id}")
    public String bulletinDetailView(@CurrentUser Member member, @PathVariable Long id, Model model){
        log.info("상세보기");
        log.info(String.valueOf(id));

        Bulletin bulletin = bulletinRepository.findById(id).orElse(null);
        assert bulletin != null;
        log.info(bulletin.getContent());
//        Bulletin bulletinByWriterId = bulletinRepository.findByWriterId(bulletin.getWriterId());
        long hit = bulletin.getHit();
        hit++;
        bulletin.setHit(hit);
        bulletinRepository.save(bulletin);
        log.info("조회수 증가 성공!");

        List<BulletinComment> bulletinCommentList = bulletinCommentRepository.findAllByBulletinId(id);
        log.info(bulletin.getWriterId());

        model.addAttribute("bulletinCommentList", bulletinCommentList);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("member", member);
        return "/view/bulletinDetail";
    }

    @GetMapping("/bulletinDetail/likeNumber")
    @ResponseBody
    public String likeNumber(@CurrentUser Member member, @RequestParam("id") String id, Model model){

        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;
        long num = bulletin.getLikeNumber();
        boolean result = false;
        JsonObject jsonObject = new JsonObject();
        result = bulletin.getLikeSets().contains(member.getUserId());
        if(result){
            jsonObject.addProperty("message", "좋아요 취소!");
            bulletin.getLikeSets().remove(member.getUserId());
            num--;
            log.info("좋아요 취소");
        }else{
            jsonObject.addProperty("message", "좋아요!");
            bulletin.getLikeSets().add(member.getUserId());
            num++;
            log.info("좋아요 추가");
        }
        bulletin.setLikeNumber(num);
        bulletinRepository.save(bulletin);
        model.addAttribute(member);
        return jsonObject.toString();
    }

    @PostMapping("/comment_upload")
    public String uploadComment(@CurrentUser Member member, @RequestParam("comment") String comment,
                                @RequestParam("commentWriter") String commentWriter, @RequestParam("id") String id, Model model){
        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;
        // 그냥 바로 아이디 넣어도 됨...
        long bulletinId = bulletin.getId();

        BulletinComment bulletinComment = BulletinComment.builder()
                .commentWriter(commentWriter)
                .bulletinId(bulletinId)
                .comment(comment)
                .date(LocalDateTime.now())
                .build();
        model.addAttribute(member);
        bulletinCommentRepository.save(bulletinComment);

        return "redirect:/bulletinDetail/"+bulletin.getId();
    }

    @GetMapping("/bulletinDetail/deleteBulletin")
    @ResponseBody
    public String deleteBulletin(@RequestParam("id")String id, @RequestParam("writerId")String writerId){
        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;

        JsonObject jsonObject = new JsonObject();
        boolean result = true;
        bulletinRepository.deleteById(Long.valueOf(id));
        List<BulletinComment> deleteBulletinCommentList = bulletinCommentRepository.findAllByBulletinId(Long.valueOf(id));

        for (BulletinComment bulletinComment : deleteBulletinCommentList) {
            long deleteId = bulletinComment.getId();
            bulletinCommentRepository.deleteById(deleteId);
        }


        jsonObject.addProperty("message", "글 삭제 성공!");

        return jsonObject.toString();
    }

    @GetMapping("/updateBulletin/{id}")
    public String updateBulletinView(@PathVariable Long id, @CurrentUser Member member, Model model ){
        log.info("글 수정 시작");
        log.info(String.valueOf(id));

        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;

        model.addAttribute("updateBulletin", bulletin);
        model.addAttribute(member);
        model.addAttribute("id", id);

        return "/view/updateBulletin";
    }

    @PostMapping("/updateBulletin")
    public String updateBulletin(long id, String title, String content, Model model, Member member){
        Bulletin updateBulletin = bulletinRepository.findById(id).orElse(null);
        assert updateBulletin != null;

        updateBulletin.setTitle(title);
        updateBulletin.setContent(content);
        bulletinRepository.save(updateBulletin);
        log.info("글 수정 성공!");

        model.addAttribute(member);
        return "redirect:/bulletin";
    }


}