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
import toeicLab.toeicLab.service.Pagination;
import toeicLab.toeicLab.repository.BulletinCommentRepository;
import toeicLab.toeicLab.repository.BulletinRepository;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.user.CurrentUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinRepository bulletinRepository;
    private final BulletinCommentRepository bulletinCommentRepository;
    private final MemberRepository memberRepository;

    /**
     * 현재 게시판에 등록되어 있는 글 제목, 작성자, 일시, 내용, 조회수, 좋아요 정보를 전부 가져옵니다.
     * @param member
     * @param model
     * @param page
     * @return view/bulletin
     */
    @GetMapping("/bulletin")
    public String bulletin(Member member, Model model, @RequestParam(defaultValue = "1") int page) {

            List<Bulletin> bulletinList = bulletinRepository.findAll();
            int size = bulletinList.size();
            int totalListCnt = size;
            Pagination pagination = new Pagination(totalListCnt, page);

            int startIndex = pagination.getStartIndex();
            int pageSize = pagination.getPageSize();
            int pageCheck = startIndex + pageSize;

            List<Bulletin> bulletinList2 = new ArrayList<>();

            if(totalListCnt == 0){
                pagination.setEndPage(1);
                pagination.setNextBlock(1);
                pagination.setTotalPageCnt(1);

                model.addAttribute(member);
                model.addAttribute("bulletinList", bulletinList2);
                model.addAttribute("pagination", pagination);
                return "view/bulletin";
            }

            for (int i = startIndex; i < pageCheck; i++){
                Bulletin bulletin = bulletinList.get(i);
                bulletinList2.add(bulletin);

                if(i == (totalListCnt-1)){
                    pageCheck = totalListCnt-1;
                }

            }
            model.addAttribute(member);
            model.addAttribute("bulletinList", bulletinList2);
            model.addAttribute("pagination", pagination);

        model.addAttribute(member);
        return "view/bulletin";
    }

    /**
     * 사용자가 선택한 게시물의 수정페이지로 이동합니다.
     * @param member
     * @param model
     * @return view/bulletin_upload
     */
    @GetMapping("/bulletin_upload")
    public String showBulletinUploadView(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);

        return "view/bulletin_upload";
    }

    /**
     * 사용자가 입력한 내용들로 게시판의 내용을 저장합니다.
     * @param member
     * @param model
     * @param title
     * @param content
     * @param memberId
     * @return redirect:/bulletin
     */
    @PostMapping("/bulletin_upload")
    public String uploadBulletin(@CurrentUser Member member , Model model, String title, String content, String memberId){
        Member needMemberNickname = memberRepository.findByUserId(memberId);
        String nickname = needMemberNickname.getNickname();

        Bulletin bulletin = Bulletin.builder()
                .title(title)
                .content(content)
                .writerId(memberId)
                .nickname(nickname)
                .date(LocalDateTime.now())
                .hit(0L)
                .likeNumber(0L)
                .build();
        model.addAttribute(member);
        bulletinRepository.save(bulletin);
        return "redirect:/bulletin";
    }

    /**
     * 사용자가 선택한 게시판의 게시물의 상세보기 페이지로 이동합니다.
     * @param member
     * @param id
     * @param model
     * @return view/bulletinDetail
     */
    @GetMapping("/bulletinDetail/{id}")
    public String bulletinDetailView(@CurrentUser Member member, @PathVariable Long id, Model model){
        Bulletin bulletin = bulletinRepository.findById(id).orElse(null);
        assert bulletin != null;

        long hit = bulletin.getHit();
        hit++;
        bulletin.setHit(hit);
        bulletinRepository.save(bulletin);

        List<BulletinComment> bulletinCommentList = bulletinCommentRepository.findAllByBulletinId(id);

        model.addAttribute("bulletinCommentList", bulletinCommentList);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("member", member);
        return "view/bulletinDetail";
    }

    /**
     * 사용자가 게시물의 좋아요를 눌렀을 경우 좋아요 수를 1 증가시키거나 취소합니다.
     * @param member
     * @param id
     * @param model
     * @return
     */
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
        }else{
            jsonObject.addProperty("message", "좋아요!");
            bulletin.getLikeSets().add(member.getUserId());
            num++;
        }
        bulletin.setLikeNumber(num);
        bulletinRepository.save(bulletin);
        model.addAttribute(member);
        return jsonObject.toString();
    }

    /**
     * 사용자가 게시물에 입력한 댓글을 저장합니다.
     * @param member
     * @param comment
     * @param commentWriter
     * @param id
     * @param model
     * @return redirect:/bulletinDetail/" + bulletin.getId()
     */
    @PostMapping("/comment_upload")
    public String uploadComment(@CurrentUser Member member, @RequestParam("comment") String comment,
                                @RequestParam("commentWriter") String commentWriter, @RequestParam("id") String id, Model model){
        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;
        long bulletinId = bulletin.getId();

        BulletinComment bulletinComment = BulletinComment.builder()
                .commentWriter(commentWriter)
                .bulletinId(bulletinId)
                .comment(comment)
                .date(LocalDateTime.now())
                .build();
        model.addAttribute(member);
        bulletinCommentRepository.save(bulletinComment);

        return "redirect:/bulletinDetail/" + bulletin.getId();
    }

    /**
     * 사용자가 선택한 게시판의 게시물을 삭제합니다.
     * @param id
     * @param writerId
     * @return
     */
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

    /**
     * 사용자가 선택한 게시물의 수정페이지로 이동동합니다.
     * @param id
    * @param member
     * @param model
     * @return view/updateBulletin
     */
    @GetMapping("/updateBulletin/{id}")
    public String updateBulletinView(@PathVariable Long id, @CurrentUser Member member, Model model ){
        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;

        model.addAttribute("updateBulletin", bulletin);
        model.addAttribute(member);
        model.addAttribute("id", id);

        return "view/updateBulletin";
    }

    /**
     * 사용자가 새로 입력한 내용들로 기존 게시판의 내용을 수정합니다.
     * @param id
     * @param title
     * @param content
     * @param model
     * @param member
     * @return redirect:/bulletin
     */
    @PostMapping("/updateBulletin")
    public String updateBulletin(long id, String title, String content, Model model, Member member){
        Bulletin updateBulletin = bulletinRepository.findById(id).orElse(null);
        assert updateBulletin != null;

        updateBulletin.setTitle(title);
        updateBulletin.setContent(content);
        bulletinRepository.save(updateBulletin);

        model.addAttribute(member);
        return "redirect:/bulletin";
    }


}
