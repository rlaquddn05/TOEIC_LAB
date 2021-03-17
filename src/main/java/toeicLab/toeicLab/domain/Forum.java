package toeicLab.toeicLab.domain;

import lombok.*;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private LocalDateTime date;

    @OneToMany(mappedBy = "parent",fetch = FetchType.LAZY)
    private List<ForumComment> comments = new ArrayList<>();

    private Long hit;

    private Long likeNumber;



}
