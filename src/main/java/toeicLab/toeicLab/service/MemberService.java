package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.*;
import toeicLab.toeicLab.user.MemberUser;
import toeicLab.toeicLab.user.SignUpForm;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ReviewNoteRepository reviewNoteRepository;
    private final QuestionRepository questionRepository;
    private final QuestionSetRepository questionSetRepository;
    private final WordRepository wordRepository;
    private final QuestionSetService questionSetService;

    /**
     * 회원가입시 사용자가 입력한 정보를 DB에 저장한다.
     * @param signUpForm
     * @return member
     */
    public Member createNewMember(SignUpForm signUpForm) {
        Member member = Member.builder()
                .userId(signUpForm.getUserId())
                .password("{noop}" + signUpForm.getPassword())
                .nickname(signUpForm.getNickname())
                .role("ROLE_USER")
                .age(signUpForm.getAge())
                .provider("toeicLab")
                .email(signUpForm.getEmail())
                .contact(signUpForm.getContact())
                .address(Address.builder()
                        .zipcode(signUpForm.getZipcode())
                        .city(signUpForm.getCity())
                        .street(signUpForm.getStreet())
                        .build())
                .memberType(MemberType.USER)
                .build();

        if(signUpForm.getGender().equals("male")){
            member.setGenderType(GenderType.MALE);
        }else {
            member.setGenderType(GenderType.FEMALE);
        }

        memberRepository.save(member);
        return member;
    }

    /**
     * 사용자가 입력한 아이디와 비밀번호의 유무와 일치여부를 DB에서 확인한다.
     * @param userId
     * @return new MemberUser(member)
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);

        if(member == null) {
            throw new UsernameNotFoundException(userId);
        }
        return new MemberUser(member);
    }

    /**
     * 자동으로 로그인을 유지하는 기능
     * @param member
     */
    public void autologin(Member member) {
        MemberUser memberUser = new MemberUser(member);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser,
                        memberUser.getMember().getPassword(),
                        memberUser.getAuthorities()
                );

        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
    }

    /**
     * 비밀번호 초기화시 사용자의 아이디를 확인한 후 등록된 이메일로 token을 전송한다.
     * @param userId
     * @param email
     * @return member
     */
    public Member sendResetPasswordEmail(String userId, String email) {
        Member member = memberRepository.findByUserId(userId);

        if(member == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        boolean emailCheck = member.getEmail().equals(email);

        if(!emailCheck){
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        return member;
    }

    /**
     * 사용자에게 입력받은 새로운 비밀번호를 저장한다.(비밀번호 초기화)
     * @param email
     * @param password
     */
    public void resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword("{noop}" + password);
        memberRepository.save(member);
    }

    /**
     * 입력받은 이메일로 사용자의 아이디를 DB에서 조회한다.
     * @param email
     * @return member
     */
    public Member sendFindIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        return member;
    }

    /**
     * 사용자가 추가하고 싶은 문제를 오답노트에 저장한다.
     * @param member
     * @param questionId
     * @param answer
     * @return boolean
     */
    @Transactional
    public boolean addReviewNote(Member member, Long questionId, String answer) {
        Question question = questionRepository.getOne(questionId);
        ReviewNote reviewNote = reviewNoteRepository.findByMember(member);
        if (reviewNote == null){
            reviewNote = new ReviewNote();
            reviewNote.setMember(member);
        }
        List<Question> qlist = reviewNote.getQuestions();
        Map<Long, String> alist = reviewNote.getSubmittedAnswers();

        if(qlist.contains(question)){
            return false;
        } else {

            qlist.add(question);
            alist.put(questionId, answer);
            reviewNote.setQuestions(qlist);
            reviewNote.setSubmittedAnswers(alist);
            reviewNoteRepository.save(reviewNote);
            return true;
        }
    }

    /**
     * 사용자가 삭제하고 싶은 문제를 오답노트에서 삭제한다.
     * @param member
     * @param questionId
     */
    public void deleteReviewNote(Member member, Long questionId) {
        Question question = questionRepository.getOne(questionId);
        ReviewNote reviewNote = reviewNoteRepository.findByMember(member);
        List<Question> list = reviewNote.getQuestions();
        Map <Long, String> alist = reviewNote.getSubmittedAnswers();
        for(int i=0; i<list.size(); ++i){
            if(list.get(i) == question){
                list.remove(i);
                alist.remove(i);

                reviewNote.setQuestions(list);
                reviewNote.setSubmittedAnswers(alist);
                reviewNoteRepository.save(reviewNote);
            }
        }
        return;
    }

    /**
     * 사용자가 맞추고 틀린 문제를 통해 정답률을 도출한다.
     * @param member
     * @param questionType
     * @return new int[]{correctCount, totalCount - correctCount}
     */
    public int[] numberOfCorrectAndWrongAnswersByQuestionType(Member member, QuestionType questionType) {
        List<QuestionSet> questionSets = questionSetRepository.getAllByMember(member);
        int correctCount = 0;
        int totalCount = 0;

        for (QuestionSet qs : questionSets) {
            Map<Long, String> submittedAnswersForQs = qs.getSubmittedAnswers();
            for (Map.Entry<Long, String> entry : submittedAnswersForQs.entrySet()) {
                Question question = questionRepository.getOne(entry.getKey());
                if (question.getQuestionType() == questionType) {
                    totalCount++;
                    if(question.getAnswer().equals(entry.getValue())){
                        correctCount++;
                    }
                }
            }
        }
        return new int[]{correctCount, totalCount - correctCount};
    }

    /**
     * 사용자가 추가하고 싶은 단어를 단어장에 추가한다.
     * @param member
     * @param word
     * @param meaning
     * @return boolean
     */
    public boolean addWordList(Member member, String word, String meaning) {
        Word wordList = wordRepository.findByMember(member);
        if (wordList == null){
            wordList = new Word();
            wordList.setMember(member);
        }
        Map<String, String> map = wordList.getWord();
        for (Map.Entry<String, String> m : map.entrySet()) {
            if (m.getKey().contains(word)) {
                return false;
            }
        }
        map.put(word, meaning);
        wordList.setWord(map);
        wordRepository.save(wordList);
        return true;
    }

    /**
     * 사용자가 삭제하고 싶은 단어를 단어장에서 삭제한다.
     * @param member
     * @param word
     */
    public void deleteWord(Member member, String word) {
        Word wordList = wordRepository.findByMember(member);
        Map<String, String> map = wordList.getWord();
        map.remove(word);
        wordList.setWord(map);
        wordRepository.save(wordList);
    }

    /**
     * 사용자의 정답률에 따라 comment를 생성하고 보여준다.
     * @param member
     * @param questionType
     * @return comment
     */
    public String createCommentByQuestionType(Member member, QuestionType questionType) {
        String comment = "생성된 코멘트가 없습니다";
        int[] correctAndWrong = numberOfCorrectAndWrongAnswersByQuestionType(member, questionType);
        int total = correctAndWrong[0] + correctAndWrong[1];
        if (total == 0) {
            return comment;
        }
        int percentage = correctAndWrong[0] / total * 100;
        if (total < 10) {
            comment = "현재 " + total + "문제 풀었습니다. 정확한 평가를 위해 더 많은 문제를 푸세요.";
        } else {
            if (percentage > 80) {
                comment = "정답률이 " + percentage + "% 로 높은 편입니다.";
            }
            if (percentage < 50) {
                comment = "정답률이 " + percentage + "% 로 낮은 편입니다.";
            }
        }
        return comment;
    }

    /**
     * 문제별로 사용자의 진행상황을 보여준다.
     * @param questionSet
     * @return date + message + percentage
     */
    public String CreateProgressByQuestionSet(QuestionSet questionSet){
        String date = questionSet.getCreatedAt().format(DateTimeFormatter.ofPattern("yy년MM월dd일HH시"));
        String percentage = questionSetService.getPercentage(questionSet);
        return date + "에 실시한 학습의 정답률 : " + percentage;
    }

    /**
     * 사용자가 푼 문제를 통해 사용자의 레벨을 계산하고 보여준다.
     * @param member
     * @return
     */
    public void CreateLevelByAllQuestions(Member member) {
        int total = 0;
        int correct = 0;
        List<QuestionSet> questionSetList = questionSetRepository.getAllByMember(member);
        if(questionSetList == null) return;
        for (QuestionSet qs : questionSetList) {
            if (qs.getQuestionSetType().toString().equals("PRACTICE") ||qs.getQuestionSetType().toString().equals("MEETING")) continue;
            Map<Long, String> submittedAnswersForQs = qs.getSubmittedAnswers();
            for (Map.Entry<Long, String> entry : submittedAnswersForQs.entrySet()) {
                Question question = questionRepository.getOne(entry.getKey());
                total++;
                if (question.getAnswer().equals(entry.getValue())) {
                    correct++;
                }
            }
        }
        if(total==0) return;
        if (correct * 100 / total < 30) member.setLevelType(LevelType.BEGINNER);
        if (correct * 100 / total >= 30 && correct * 100 / total < 80 ) member.setLevelType(LevelType.INTERMEDIATE);
        if (correct * 100 / total >= 80) member.setLevelType(LevelType.ADVANCED);
        memberRepository.save(member);
        return;
    }

}
