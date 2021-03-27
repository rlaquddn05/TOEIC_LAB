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
import toeicLab.toeicLab.repository.ReviewNoteRepository;
import toeicLab.toeicLab.user.MemberUser;
import toeicLab.toeicLab.user.SignUpForm;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewNoteRepository reviewNoteRepository;
    private final QuestionRepository questionRepository;

    public Member createNewMember(SignUpForm signUpForm) {
        Member member = Member.builder()
                .userId(signUpForm.getUserId())
                .password(signUpForm.getPassword())
                .nickname(signUpForm.getNickname())
                .age(signUpForm.getAge())
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

        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
        return member;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(username);
        log.info(member.getUserId());
        if(member == null) {
            throw new UsernameNotFoundException(username);
        }
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
        member.setPassword(password);
        member.encodePassword(passwordEncoder);
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
    public boolean addReviewNote(Member member, Long questionId) {
        Question question = questionRepository.getOne(questionId);
        ReviewNote reviewNote = reviewNoteRepository.findByMember(member);
        if (reviewNote == null){
            reviewNote = new ReviewNote();
            reviewNote.setMember(member);
        }
        List<Question> list = reviewNote.getQuestions();

        if(list.contains(question)){
            list.remove(question);
            reviewNote.setQuestions(list);
            reviewNoteRepository.save(reviewNote);
            return false;
        } else {

            list.add(question);
            reviewNote.setQuestions(list);
            reviewNoteRepository.save(reviewNote);
            return true;
        }
    }
}
