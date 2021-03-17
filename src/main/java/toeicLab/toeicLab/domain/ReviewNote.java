package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewNote {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;

    @OneToMany
    private List<Question> questions = new ArrayList<>();

    @ElementCollection
    private List<String> submittedAnswers = new ArrayList<>();

}
