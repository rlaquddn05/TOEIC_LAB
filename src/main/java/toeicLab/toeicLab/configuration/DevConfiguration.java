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
import toeicLab.toeicLab.service.StudyGroupApplicationService;

import javax.annotation.PostConstruct;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DevConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupApplicationService studyGroupApplicationService;
    private final StudyGroupApplicationRepository studyGroupApplicationRepository;
    private final int NUMBER_OF_DUMMY_USERS = 20;
    private final LevelType[] levelTypes = {LevelType.BEGINNER,
            LevelType.INTERMEDIATE, LevelType.ADVANCED};
    private final GenderType[] genderTypes = {GenderType.MALE, GenderType.FEMALE};
    private final int[] tagValues = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    @PostConstruct
    public void createDummyUsers() {
        for (int i = 1; i <= NUMBER_OF_DUMMY_USERS; i++) {
            Member member = Member.builder()
                    .email("dummy" + i + "@a.a")
                    .password(passwordEncoder.encode("1234"))
                    .memberType(MemberType.USER)
                    .levelType(levelTypes[(int)(Math.random()*3)])
                    .age((int)(Math.random()*25)+10)
                    .genderType(genderTypes[(int)(Math.random()*2)])
                    .build();
            memberRepository.save(member);

            int value=1;
            for(int j : tagValues){
                value *= Math.random()>0.5?j:1;
            }
            if(value==1){
                value=2*3*5*7*11*13*17*19*23*29;
            }
            StudyGroupApplication studyGroupApplication = StudyGroupApplication.builder()
                    .member(member)
                    .value(value)
                    .build();
            studyGroupApplicationRepository.save(studyGroupApplication);
        }
        log.info("DummyUsers created.");
    }

    @PostConstruct
    public void testMatchStudyGroup(){
        studyGroupApplicationService.matchStudyGroups();
    }

    @PostConstruct
    public void createTestUsers() {
        Member member = Member.builder()
                .email("a@a.a")
                .password(passwordEncoder.encode("1234"))
                .memberType(MemberType.USER)
                .levelType(levelTypes[(int)(Math.random()*3)])
                .age((int)(Math.random()*25)+10)
                .genderType(genderTypes[(int)(Math.random()*2)])
                .build();

        memberRepository.save(member);
        log.info("a@a.a created.");
    }

}
