package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;

    private String content;

    private boolean checked;

    private LocalDateTime date;
}
