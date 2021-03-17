package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.MemberType;
import toeicLab.toeicLab.repository.MemberRepository;

import javax.annotation.PostConstruct;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DevConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void createTestUser(){
        Member member = Member.builder()
                .email("a@a.a")
                .password(passwordEncoder.encode("1234"))
                .memberType(MemberType.USER)
                .build();

        memberRepository.save(member);
        log.info("TestUser (a@a.a) has been created.");
    }
}
