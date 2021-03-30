package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.QuestionRepository;
import toeicLab.toeicLab.repository.QuestionSetRepository;
import toeicLab.toeicLab.repository.ReviewNoteRepository;
import toeicLab.toeicLab.user.MemberUser;
import toeicLab.toeicLab.user.SignUpForm;
import toeicLab.toeicLab.user.UpdateForm;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewNoteRepository reviewNoteRepository;
    private final QuestionRepository questionRepository;
    private final QuestionSetRepository questionSetRepository;


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

//        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
        return member;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);
        log.info("[ToeicLab]으로 로그인" + member.getUserId());

        if(member == null) {
            throw new UsernameNotFoundException(userId);
        }
        log.info(member.getUserId());
        return new MemberUser(member);
    }

    public void autologin(Member member) {

        MemberUser memberUser = new MemberUser(member);

        // 인증 토큰 생성
        // SecurityContext 가 인증 유저를 관리 (현재 로그인 중인 유저들의 정보)
        // 유저 구분은 AuthenticationToken 으로 관리됨.
        // Token 구조 : username, password, role 으로 구성됨.
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser, //member.getEmail(),  // username
                        memberUser.getMember().getPassword(), // password
                        memberUser.getAuthorities() // authorities (권한들 (Collection 객체))
                );

        // SecurityContext 에 token 저장
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);

    }


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

    public void resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword("{noop}" + password);
//        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
    }


    public Member sendFindIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        return member;
    }

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

    public int[] numberOfCorrectAndWrongAnswersByQuestionType(Member member, QuestionType questionType) {

        List<QuestionSet> questionSets = questionSetRepository.getAllByMember(member);
        int correctCount = 0;
        int totalCount = 0;

        for (QuestionSet qs : questionSets) {
            Map<Long, String> submittedAnswersForQs = qs.getSubmittedAnswers();
            totalCount += submittedAnswersForQs.size();
            for (Map.Entry<Long, String> entry : submittedAnswersForQs.entrySet()) {
                Question question = questionRepository.getOne(entry.getKey());
                if (question.getQuestionType() == questionType && question.getAnswer().equals(entry.getValue())) {
                    correctCount++;
                }
            }
        }
        return new int[]{correctCount, totalCount - correctCount};
    }

    public String createCommentByQuestionType(Member member, QuestionType questionType) {
        String comment = "생성된 코멘트가 없습니다";
        int[] correctAndWrong = numberOfCorrectAndWrongAnswersByQuestionType(member, questionType);
        int total = correctAndWrong[0] + correctAndWrong[1];
        int percentage = correctAndWrong[1] / total * 100;
        if (total < 10) {
            comment = "현재 " + questionType.toString() + " 을 " + total + " 문제 푸셨습니다. 정확한 평가를 위해서는 더 많은 문제를 풀어주세요.";
        } else {
            if (percentage > 80) {
                comment = questionType.toString() + " 의 정답률이 " + percentage + "% 로 높은 편입니다.";
            }
            if (percentage < 50) {
                comment = questionType.toString() + " 의 정답률이 " + percentage + "% 로 낮은 편입니다.";
            }
        }
        return comment;
    }

}
