package toeicLab.toeicLab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toeicLab.toeicLab.domain.StudyGroupApplication;

@Repository
public interface StudyGroupApplicationRepository extends JpaRepository<StudyGroupApplication, Long> {

}
