package toeicLab.toeicLab.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class StudyGroup {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name="studyGroup_member",
            joinColumns = @JoinColumn(name = "studyGroup_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.REMOVE)
    private List<Meeting> meetings = new ArrayList<>();

    private Long readerId;

//    @ElementCollection
//    private List<String> content = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Comment> comments;

}
