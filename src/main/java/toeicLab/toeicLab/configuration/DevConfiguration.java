package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import toeicLab.toeicLab.domain.*;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.repository.StudyGroupApplicationRepository;
import toeicLab.toeicLab.repository.StudyGroupRepository;

import javax.annotation.PostConstruct;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DevConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;
    private final int NUMBER_OF_DUMMY_USERS = 1000;
    private final LevelType[] levelTypes = {LevelType.BEGINNER,
            LevelType.INTERMEDIATE, LevelType.ADVANCED};
    private final GenderType[] genderTypes = {GenderType.MALE, GenderType.FEMALE};

    @PostConstruct
    public void createDummyUsers() {
        for (int i = 1; i <= NUMBER_OF_DUMMY_USERS; i++) {
            Member member = Member.builder()
                    .email("dummy" + i + "@a.a")
                    .password(passwordEncoder.encode("1234"))
                    .memberType(MemberType.USER)
                    .levelType(levelTypes[(int)(Math.random()*3+1)])
                    .age((int)(Math.random()*25+10))
                    .genderType(genderTypes[(int)(Math.random()*2+1)])
                    .build();
            memberRepository.save(member);
        }
        log.info("DummyUsers created.");
    }
}
