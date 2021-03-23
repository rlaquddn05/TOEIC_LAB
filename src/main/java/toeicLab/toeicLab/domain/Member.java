package toeicLab.toeicLab.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private String gender;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    private String email;

    private String contact;

    private int age;

    private LocalDateTime joinedAt;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private LevelType levelType;

    @OneToMany(mappedBy = "member")
    private List<QuestionSet> questionSetList = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<StudyGroup> studyGroupList = new ArrayList<>();

    @OneToOne(mappedBy = "member")
    private StudyGroupApplication studyGroupApplication;

    @OneToMany
    private List<Word> wordList = new ArrayList<>();


    @OneToMany(mappedBy = "member")
    private List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ReviewNote> reviewNoteList = new ArrayList<>();

    @OneToMany
    private List<Schedule> schedules = new ArrayList<>();
<<<<<<< HEAD


    private String emailCheckToken;

    //TODO passwordEncoder 추가
=======
    

>>>>>>> ddb7f79df27165e3c970503e0a24e69ae9bf1a2a
    @Transactional
    public void encodePassword(PasswordEncoder passwordEncoder){

        password = passwordEncoder.encode(password);
    }

}
