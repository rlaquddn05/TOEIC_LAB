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
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.service.*;
import toeicLab.toeicLab.user.CurrentUser;
import toeicLab.toeicLab.user.StudyGroupApplicationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MyFunctionController {

    private final WordRepository wordRepository;
    private final ReviewNoteRepository reviewNoteRepository;
    private final QuestionSetRepository questionSetRepository;
    private final MemberRepository memberRepository;
    private final QuestionService questionService;
    private final MemberService memberService;

    /**
     * 사용자가 문제풀이를 한 뒤에 자신의 학습현황 페이지로 이동합니다.
     * @param member
     * @param model
     * @return function/my_progress
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
        return "function/my_progress";
    }

    /**
     * 기존 페이지에 팝업사전(새 창)을 띄웁니다.
     * @param member
     * @param model
     * @return function/popup_dictionary
     */
    @GetMapping("/popup_dictionary")
    public String popupLayout(@CurrentUser Member member, Model model) {
        model.addAttribute("member", member);
        return "function/popup_dictionary";
    }

    /**
     * 단어검색시 한글은 영어로 영어는 한글로 검색한 결과를 보여줍니다.(네이버 검색 페이지 실시간 크롤링)
     * @param word
     * @return ajax data
     * @throws Exception
     */
    @RequestMapping("/popup_dictionary_find/{word}")
    @ResponseBody
    public String dictionary(@PathVariable String word) throws Exception {
        String result;
        String[] splitedWord = word.split("");
        if (Pattern.matches("[a-zA-Z]", splitedWord[0])) {
            String loweredWord = word.toLowerCase();
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + loweredWord + "뜻";
            Document document = Jsoup.connect(url).get();
            Elements element = document.getElementsByAttributeValue("class", "mean api_txt_lines");
            String cut = element.get(0).text();
            result = cut;

            return "{" + "\"" + "word" + "\"" + ":" + "\"" + word + "\"" + "," + "\"" + "meaning" + "\"" + ":" + "\"" + result + "\"" + "}";

        } else {
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + word + "영어로";
            Document document = Jsoup.connect(url).get();
            Elements element = document.getElementsByAttributeValue("class", "mean api_txt_lines");
            String cut = element.get(0).text();
            result = cut;

            return "{" + "\"" + "word" + "\"" + ":" + "\"" + result + "\"" + "," + "\"" + "meaning" + "\"" + ":" + "\"" + word + "\"" + "}";
        }

    }

    /**
     * 검색한 단어를 단어장에 추가합니다.
     * @param member
     * @param word
     * @param meaning
     * @return jsonObject
     */
    @RequestMapping("/add_word_list")
    @ResponseBody
    public String addWordList(@CurrentUser Member member, @RequestParam("word") String word, @RequestParam("meaning") String meaning) {
        String w = word.replaceAll("\"", " ").trim();
        String m = meaning.replaceAll("\"", " ").trim();
        JsonObject jsonObject = new JsonObject();
        boolean result = false;

        try {
            result = memberService.addWordList(member, w, m);
            if (result) {
                jsonObject.addProperty("message", "단어장에 추가");
            } else {
                jsonObject.addProperty("message", "단어장에 이미 존재합니다.");
            }
        } catch (IllegalArgumentException e) {
            jsonObject.addProperty("message", "잘못된정보");
        }

        return jsonObject.toString();
    }

    /**
     * 사용자가 추가한 단어들을 볼 수 있는 단어장페이지로 이동합니다.
     * @param member
     * @param model
     * @return function/my_vocabulary_list
     */
    @GetMapping("/my_vocabulary_list")
    public String myVocabularyList(@CurrentUser Member member, Model model) {
        Word wordList = wordRepository.findByMember(member);
        if (wordList == null) {
            wordList = new Word();
            model.addAttribute("exist", "noWordList");
        }
        Map<String, String> map = wordList.getWord();

        if (map.isEmpty()){
            model.addAttribute("exist", "noWord");
        }
        model.addAttribute("wordList", map);
        model.addAttribute(member);

        return "function/my_vocabulary_list";
    }

    /**
     * 단어장에서 사용자가 선택한 단어를 삭제합니다.
     * @param member
     * @param model
     * @param word
     * @return redirect:/my_vocabulary_list
     */
    @GetMapping("/delete_word/{word}")
    public String DeleteTest(@CurrentUser Member member, Model model, @PathVariable String word) {
        memberService.deleteWord(member, word);
        model.addAttribute("member", member);

        return "redirect:/my_vocabulary_list";
    }

    /**
     * 사용자가 추가한 문제들을 볼 수 있는 오답노트페이지로 이동합니다.
     * @param member
     * @param model
     * @return function/my_review_note
     */
    @GetMapping("/my_review_note")
    public String myReviewNote(@CurrentUser Member member, Model model) {
        ReviewNote reviewNote = reviewNoteRepository.findByMember(member);
        if(reviewNote == null){
            reviewNote = new ReviewNote();
            model.addAttribute("exist", "noReviewNote");
        }
        List<Question> list = reviewNote.getQuestions();
        if(list.isEmpty()){
            model.addAttribute("exist", "noQuestion");
        }
        QuestionSet questionSet = new QuestionSet();
        questionSet.setQuestions(list);
        List<String> str = new ArrayList<>();
        questionService.checkTypeList(questionSet, str);

        model.addAttribute("questionType", str);
        model.addAttribute("questionList", list);
        model.addAttribute("member", member);
        model.addAttribute("userAnswer", reviewNote.getSubmittedAnswers());
        return "function/my_review_note";
    }

    /**
     * 사용자가 원하는 문제를 오답노트에 추가합니다.
     * @param member
     * @param id
     * @param answer
     * @return jsonObject
     */
    @GetMapping("/add_review_note")
    @ResponseBody
    public String AddReviewNote(@CurrentUser Member member, @RequestParam("id") Long id, @RequestParam("answer") String answer){
        JsonObject jsonObject = new JsonObject();
        boolean result = false;
        try {
            result = memberService.addReviewNote(member, id, answer);
            if(result) {
                jsonObject.addProperty("message", "오답노트에 추가되었습니다.");
            }
            else {
                jsonObject.addProperty("message", "오답노트에 이미 있습니다.");
            }

        } catch (IllegalArgumentException e){
            jsonObject.addProperty("message", "잘못된정보");
        }
        return jsonObject.toString();
    }

    /**
     * 사용자가 선택한 문제를 오답노트에서 삭제합니다.
     * @param member
     * @param id
     * @return jsonObject
     */
    @GetMapping("/delete_review_note")
    @ResponseBody
    public String TestReviewNote(@CurrentUser Member member, @RequestParam("id") Long id){
        JsonObject jsonObject = new JsonObject();
        try {
            memberService.deleteReviewNote(member, id);
            jsonObject.addProperty("message", "오답노트에서 삭제되었습니다.");
        }
        catch (Exception e){
            jsonObject.addProperty("message", "예상치 못한 오류");
        }
        return jsonObject.toString();
    }
}
