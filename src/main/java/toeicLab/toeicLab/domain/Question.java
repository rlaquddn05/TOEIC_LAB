package toeicLab.toeicLab.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Question {

    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private String image;

    private int smallSetType;

    private int smallSetId;

}
