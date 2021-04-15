package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toeicLab.toeicLab.domain.Forum;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.Question;
import toeicLab.toeicLab.repository.ForumRepository;
import toeicLab.toeicLab.user.CurrentUser;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumRepository forumRepository;

    /**
     * 사용자가 입력한 문제등록 관련 정보들을 게시글 양식으로 DB에 저장한다.
     * @param member
     * @param title
     * @param q
     */
    public void addForum(@CurrentUser Member member, String title, Question q) {
        Forum forum = Forum.builder()
                .title(title)
                .nickname(member.getNickname())
                .userId(member.getUserId())
                .date(LocalDateTime.now())
                .hit(0L)
                .questionId(q.getId())
                .build();
        forumRepository.save(forum);
    }
}
