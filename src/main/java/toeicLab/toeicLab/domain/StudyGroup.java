package toeicLab.toeicLab.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class StudyGroup {

    @Id @GeneratedValue
    private Long id;

    //방의 이름(필요없다면 버려도된다.)
    private String name;

    @ManyToMany
    @JoinTable(
            name="studyGroup_member",
            joinColumns = @JoinColumn(name = "studyGroup_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "studyGroup")
    private List<Meeting> meetings = new ArrayList<>();

}
