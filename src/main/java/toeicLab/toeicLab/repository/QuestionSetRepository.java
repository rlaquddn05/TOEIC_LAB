package toeicLab.toeicLab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toeicLab.toeicLab.domain.QuestionSet;

@Repository
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {
}
