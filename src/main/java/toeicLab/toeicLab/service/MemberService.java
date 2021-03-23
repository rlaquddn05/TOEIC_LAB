package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.Address;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.user.MemberUser;
import toeicLab.toeicLab.user.SignUpForm;
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member createNewMember(SignUpForm signUpForm) {
        Member member = Member.builder()
                .userId(signUpForm.getUserId())
                .password(signUpForm.getPassword())
                .nickname(signUpForm.getNickname())
                .gender(signUpForm.getGender())
                .age(signUpForm.getAge())
                .email(signUpForm.getEmail())
                .contact(signUpForm.getContact())
                .address(Address.builder()
                        .zipcode(signUpForm.getZipcode())
                        .city(signUpForm.getCity())
                        .street(signUpForm.getStreet())
                        .build())
                .build();
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
        return member;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if(member == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MemberUser(member);
    }


    public void sendResetPasswordEmail(String userId, String email) {
        Member member = memberRepository.findByEmail(userId);
        boolean emailCheck = member.getEmail().equals(email);
        if(member == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        if(!emailCheck){
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
        member.getEmail();
        // member의 이메일로 비밀번호 키 보내기


    }

    // 저장된 토큰과 같은 값인지 확인
    public boolean checkEmailToken(String email, String emailCheckToken) {
        if(email == null || emailCheckToken == null){
            return false;
        }
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            return false;
        }
        return emailCheckToken.equals(member.getEmailCheckToken());
    }

    @Transactional
    public void resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword(password);
    }


    public void sendFindIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        return;
    }
}
