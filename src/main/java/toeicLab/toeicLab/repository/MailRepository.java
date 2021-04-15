package toeicLab.toeicLab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import toeicLab.toeicLab.domain.MailDto;
@Repository
public interface MailRepository extends JpaRepository<MailDto, Long> {
    MailDto findByEmail(String email);
    MailDto findByEmailCheckToken(String emailCheckToken);
}
