package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import toeicLab.toeicLab.domain.GenderType;
import toeicLab.toeicLab.domain.LevelType;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.MemberType;
import toeicLab.toeicLab.repository.MemberRepository;
import toeicLab.toeicLab.service.StudyGroupApplicationService;

import javax.annotation.PostConstruct;

@Profile("service")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ServiceConfiguration {
    private final StudyGroupApplicationService studyGroupApplicationService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private static final LevelType[] levelTypes = {LevelType.BEGINNER, LevelType.INTERMEDIATE, LevelType.ADVANCED};
    private static final GenderType[] genderTypes = {GenderType.MALE, GenderType.FEMALE};

    @Scheduled(cron="0 0 04 * * ?")
    public void MatchStudyGroup() {
        studyGroupApplicationService.matchStudyGroups();
    }

    public void createTestUsers() {
        Member member = Member.builder()
                .userId("testUser")
                .email("a@a.a")
//                .password(passwordEncoder.encode("1234"))
                .password("1234")
                .memberType(MemberType.USER)
                .levelType(levelTypes[(int) (Math.random() * 3)])
                .age((int) (Math.random() * 25) + 10)
                .genderType(genderTypes[(int) (Math.random() * 2)])
                .build();

        memberRepository.save(member);
        log.info("a@a.a created.");
    }
    @PostConstruct
    public void initDummyUsers() {
        createTestUsers();
    }
}
