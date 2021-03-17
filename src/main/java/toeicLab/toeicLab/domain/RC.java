package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@DiscriminatorValue("RC")
public class RC extends Question{

    @Column
    @Lob
    private String content;

    @Column
    @Lob
    private String content2;

    @Column
    @Lob
    private String content3;

    private String answer;

    private String exampleA;

    private String exampleB;

    private String exampleC;

    private String exampleD;

    @Column
    @Lob
    private String solution;

    private int smallSetType; // 3,4,5

    private int smallSetId; // smallSet 가장 앞번호
}
