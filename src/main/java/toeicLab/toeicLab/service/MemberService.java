package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
        // 이메일 검증 토큰 생성 -> DB에 저장
//        member.generateEmailCheckToken();
//        member.encodePassword(passwordEncoder);
        memberRepository.save(member); /// 이 부분!!!
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


}
