package toeicLab.toeicLab.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bulletin {

        @Id @GeneratedValue
        private Long id;

        private String title;

        private String content;

        private LocalDateTime date;

        private String writerId;

        private String nickname;

        @ElementCollection
        private Set<String> likeSets = new HashSet<>();

        private Long hit;

        private Long likeNumber;

    }
