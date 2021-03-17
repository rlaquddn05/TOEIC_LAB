package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private StudyGroup studyGroup;

    private LocalDateTime date;

    @OneToOne
    private QuestionSet questionSet;

    private Integer count;

    private LocalDateTime duration;
}
