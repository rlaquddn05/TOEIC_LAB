package toeicLab.toeicLab.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import toeicLab.toeicLab.domain.Bulletin;
import toeicLab.toeicLab.domain.Forum;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.Word;
import toeicLab.toeicLab.repository.ForumRepository;
import toeicLab.toeicLab.repository.WordRepository;
import toeicLab.toeicLab.service.MemberService;
import toeicLab.toeicLab.user.CurrentUser;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ForumController {

    private final MemberService memberService;
    private final WordRepository wordRepository;
    private final ForumRepository forumRepository;

    @GetMapping("/forum")
    public String forum(@CurrentUser Member member, Model model) {
        List<Forum> ForumList = forumRepository.findAll();
        model.addAttribute("ForumList", ForumList);
        model.addAttribute("member", member);
        return "/view/forum";
    }

    @GetMapping("/forum_upload")
    public String uploadQuestion(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);
        return "/view/forum_upload";
    }

    @PostMapping("/forum_upload")
    public String addQuestion(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);
        log.info("test");
        return "/view/forum";
    }

    @GetMapping("/readText")
    public void readText(){
    log.info("test");
        }

    @GetMapping("/vocabulary_test")
    public String vocabularyTest(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "/view/vocabulary_test";
    }

    @GetMapping("/popup_dictionary")
    public String popupLayout(@CurrentUser Member member, Model model){
        model.addAttribute("member", member);
        return "/view/popup_dictionary";
    }

    @RequestMapping("/popup_dictionary_find/{word}")
    @ResponseBody
    public String dictionary(@PathVariable String word) throws Exception {
        String result;
        System.out.println(word);
        String[] splittedWord = word.split("");
        if(Pattern.matches("[a-zA-Z]", splittedWord[0])){
            log.info("영어 -> 한국어");
            String loweredWord = word.toLowerCase();
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + loweredWord + "뜻";
            Document document = Jsoup.connect(url).get();
            Elements element = document.getElementsByAttributeValue("class", "mean api_txt_lines");
            String cut = element.get(0).text();
//            int index = cut.indexOf("(");
//            result = cut.substring(0, index);
            result = cut;

            return "{" + "\"" + "word"  + "\"" + ":" +  "\"" + word  + "\"" + "," + "\"" + "meaning"  + "\"" +":" + "\"" + result  + "\""  + "}";

        }  else {
            log.info("한국어 -> 영어");
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + word + "영어로";
            Document document = Jsoup.connect(url).get();
            Elements element = document.getElementsByAttributeValue("class", "mean api_txt_lines");
            log.info(element.get(0).text());
            String cut = element.get(0).text();
            result = cut;

            return "{" + "\"" + "word"  + "\"" + ":" +  "\"" + result  + "\"" + "," + "\"" + "meaning"  + "\"" +":" + "\"" + word  + "\""  + "}";
        }

    }

    @RequestMapping("/add_word_list")
    @ResponseBody
    public String addWordList (@CurrentUser Member member, @RequestParam("word") String word, @RequestParam("meaning") String meaning){
//    public String addWordList (Member member, HttpServletRequest request){
        String w = word.replaceAll("\"", " ").trim();
        String m = meaning.replaceAll("\"", " ").trim();
        JsonObject jsonObject = new JsonObject();
        boolean result = false;
//        String word = request.getParameter("word");
//        String meaning = request.getParameter("meaning");
        System.out.println(w);
        System.out.println(m);

        try {
            result = memberService.addWordList(member, w, m);
            if(result) {
                jsonObject.addProperty("message", "단어장에 추가");
            }
            else {
                jsonObject.addProperty("message", "단어장에 이미 존재합니다.");
            }

        } catch (IllegalArgumentException e){
            jsonObject.addProperty("message", "잘못된정보");
        }

        return jsonObject.toString();
    }


    @GetMapping("/my_vocabulary_list")
    public String myVocabularyList(@CurrentUser Member member, Model model) {
        Word wordList = wordRepository.findByMember(member);
        System.out.println(wordList);
        if(wordList == null){
            wordList = new Word();
            model.addAttribute("exist", "noWordList");
        }
        Map<String, String> map = wordList.getWord();

        model.addAttribute("wordList", map);

        model.addAttribute(member);

        return "/view/my_vocabulary_list";
    }

    @GetMapping("/delete_word/{word}")
    public String DeleteTest(@CurrentUser Member member, Model model, @PathVariable String word){
        memberService.deleteWord(member, word);
        System.out.println(word + "삭제");

        model.addAttribute("member", member);
        return "redirect:/my_vocabulary_list";
    }

//    @GetMapping("/delete_word")
//    @ResponseBody
//    public String DeleteTest(@CurrentUser Member member, Model model, @RequestParam("word") String word){
//        memberService.deleteWord(member, word);
//        System.out.println(word + "삭제");
//
//        JsonObject jsonObject = new JsonObject();
//        boolean result = false;
//
//        try {
//            result = memberService.deleteWord(member, word);
//            if(result) {
//                jsonObject.addProperty("message", "단어장에서 제거하였습니다.");
//            }
//
//        } catch (IllegalArgumentException e){
//            jsonObject.addProperty("message", "잘못된정보");
//        }
//        model.addAttribute("member", member);
//        return jsonObject.toString();
////
////        return "redirect:/my_vocabulary_list";
//    }
}
