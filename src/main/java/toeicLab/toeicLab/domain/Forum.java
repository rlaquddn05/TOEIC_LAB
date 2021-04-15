package toeicLab.toeicLab.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forum {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String userId;

    private LocalDateTime date;

    private Long hit;

    private Long questionId;

    private String nickname;

}
