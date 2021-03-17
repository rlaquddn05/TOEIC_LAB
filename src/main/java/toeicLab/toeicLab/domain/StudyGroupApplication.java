package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupApplication {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;

    @ElementCollection
    private List<StudygroupApplicaionTag> tags;

    private LocalDateTime submitTime;

    private boolean matching;
}
