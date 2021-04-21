package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.service.*;
import toeicLab.toeicLab.user.CurrentUser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommunityController {

    private final BulletinRepository bulletinRepository;
    private final BulletinCommentRepository bulletinCommentRepository;
    private final MemberRepository memberRepository;
    private final ForumRepository forumRepository;
    private final QuestionRepository questionRepository;
    private final VisionService visionService;
    private final QuestionService questionService;
    private final ForumService forumService;

    /**
     * 현재 게시판에 등록되어 있는 글 제목, 작성자, 일시, 내용, 조회수, 좋아요 정보를 전부 가져옵니다.
     * @param member
     * @param model
     * @param page
     * @return community/bulletin
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
                return "community/bulletin";
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
        return "community/bulletin";
    }

    /**
     * 사용자가 선택한 게시물의 수정페이지로 이동합니다.
     * @param member
     * @param model
     * @return community/bulletin_upload
     */
    @GetMapping("/bulletin_upload")
    public String showBulletinUploadView(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);

        return "community/bulletin_upload";
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
     * @return community/bulletinDetail
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
        return "community/bulletinDetail";
    }

    /**
     * 사용자가 게시물의 좋아요를 눌렀을 경우 좋아요 수를 1 증가시키거나 취소합니다.
     * @param member
     * @param id
     * @param model
     * @return jsonObject
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
     * @return jsonObject
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
     * @return community/updateBulletin
     */
    @GetMapping("/updateBulletin/{id}")
    public String updateBulletinView(@PathVariable Long id, @CurrentUser Member member, Model model ){
        Bulletin bulletin = bulletinRepository.findById(Long.valueOf(id)).orElse(null);
        assert bulletin != null;

        model.addAttribute("updateBulletin", bulletin);
        model.addAttribute(member);
        model.addAttribute("id", id);

        return "community/updateBulletin";
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

    /**
     * 현재 문제등록에 게시되어있는 게시물들을 보여주는 페이지로 이동합니다.
     * @param member
     * @param model
     * @param page
     * @return community/forum
     */
    @GetMapping("/forum")
    public String forum(@CurrentUser Member member, Model model, @RequestParam(defaultValue = "1") int page) {
        List<Forum> forumList = forumRepository.findAll();

        int size = forumList.size();
        int totalListCnt = size;

        Pagination pagination = new Pagination(totalListCnt, page);

        int startIndex = pagination.getStartIndex();
        int pageSize = pagination.getPageSize();
        int pageCheck = startIndex + pageSize;

        List<Forum> forumList2 = new ArrayList<>();

        if(totalListCnt == 0){
            pagination.setEndPage(1);
            pagination.setNextBlock(1);
            pagination.setTotalPageCnt(1);

            model.addAttribute(member);
            model.addAttribute("forumList", forumList2);
            model.addAttribute("pagination", pagination);
            return "community/forum";
        }
        for (int i = startIndex; i < pageCheck; i++){
            Forum forum = forumList.get(i);

            forumList2.add(forum);

            if(i == (totalListCnt-1)){
                pageCheck = totalListCnt-1;
            }
        }

        model.addAttribute(member);
        model.addAttribute("forumList", forumList2);
        model.addAttribute("pagination", pagination);
        return "community/forum";
    }

    /**
     * 문제등록을 할 수 있는 페이지로 이동합니다.
     * @param member
     * @param model
     * @return community/forum_upload
     */
    @GetMapping("/forum_upload")
    public String uploadQuestion(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "community/forum_upload";
    }

    /**
     * 사용자가 이미지를 통해 추출한 데이터들을 게시글의 정보로 저장합니다.
     * @param member
     * @param model
     * @param title
     * @param questionType
     * @param content1
     * @param content2
     * @param content3
     * @param question
     * @param exampleA
     * @param exampleB
     * @param exampleC
     * @param exampleD
     * @param answer
     * @param solution
     * @return redirect:/forum
     */
    @PostMapping("/forum_upload")
    public String addQuestion(@CurrentUser Member member, Model model, @RequestParam String title,
                              @RequestParam String questionType, @RequestParam(required = false) String content1,
                              @RequestParam(required = false) String content2, @RequestParam(required = false) String content3,
                              @RequestParam String question, @RequestParam String exampleA,
                              @RequestParam String exampleB, @RequestParam String exampleC,
                              @RequestParam String exampleD, @RequestParam String answer,
                              @RequestParam String solution) {
        Question q = questionService.createQuestion(questionType, content1, content2, content3, question, exampleA, exampleB,
                exampleC, exampleD, answer, solution);

        forumService.addForum(member, title, q);
        model.addAttribute("member", member);
        return "redirect:/forum";
    }

    /**
     * 사용자가 선택한 게시글의 상세보기 페이지로 이동합니다.
     * @param member
     * @param id
     * @param model
     * @return community/forum_detail
     */
    @GetMapping("/forumDetail/{id}")
    public String forumDetailView(@CurrentUser Member member, @PathVariable Long id, Model model) {
        Forum forum = forumRepository.findById(id).orElse(null);
        long hit = forum.getHit();
        hit++;
        forum.setHit(hit);
        forumRepository.save(forum);

        Question question = questionRepository.findById(forum.getQuestionId()).orElse(null);
        assert question != null;

        QuestionType type = question.getQuestionType();
        if (type==QuestionType.PART1||type==QuestionType.PART2||type==QuestionType.PART3||type==QuestionType.PART4) {
            model.addAttribute("content",((LC) question).getContent());
            model.addAttribute("recording",((LC) question).getRecording());
            model.addAttribute("exampleA", ((LC) question).getExampleA());
            model.addAttribute("exampleB", ((LC) question).getExampleB());
            model.addAttribute("exampleC", ((LC) question).getExampleC());
            model.addAttribute("exampleD", ((LC) question).getExampleD());
            model.addAttribute("solution", ((LC) question).getSolution());
        }
        else {
            model.addAttribute("content", ((RC) question).getContent());
            model.addAttribute("content2", ((RC) question).getContent2());
            model.addAttribute("content3", ((RC) question).getContent3());
            model.addAttribute("exampleA", ((RC) question).getExampleA());
            model.addAttribute("exampleB", ((RC) question).getExampleB());
            model.addAttribute("exampleC", ((RC) question).getExampleC());
            model.addAttribute("exampleD", ((RC) question).getExampleD());
            model.addAttribute("solution", ((RC) question).getSolution());
        }
        model.addAttribute("forum", forum);
        model.addAttribute("member", member);
        model.addAttribute("question", question);
        model.addAttribute("type", question.getQuestionType());
        model.addAttribute("answer", question.getAnswer());
        model.addAttribute("questionExplanation", question.getQuestionExplanation());

        return "community/forum_detail";
    }

    /**
     * 사용자가 추출하려는 문제의 이미지를 업로드합니다.
     * @param member
     * @param model
     * @param file
     * @return loadText
     * @throws IOException
     * @throws IllegalStateException
     */
    @RequestMapping(value="/upload_img", method = RequestMethod.POST)
    @ResponseBody
    public StringBuilder upload(@CurrentUser Member member, Model model, @RequestParam("file") MultipartFile file) throws IOException, IllegalStateException{
        StringBuilder loadText = new StringBuilder();
        FileOutputStream fos = null;

        try {
            byte fileData[] = file.getBytes();
            fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+file.getName()+".jpg");
            fos.write(fileData);

        } finally {
            if (fos!= null){
                try {
                    fos.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        visionService.readText(file.getName(), loadText);

        model.addAttribute(member);
        return loadText;
    }

}
