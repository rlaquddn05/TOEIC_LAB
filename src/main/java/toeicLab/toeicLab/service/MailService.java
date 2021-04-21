package toeicLab.toeicLab.service;

import lombok.AllArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import toeicLab.toeicLab.domain.MailDto;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "YOUR_EMAIL_ADDRESS";

    /**
     * 회원가입시 사용자의 이메일로 token을 전송한다.
     * @param mailDto
     * @return mailDto
     */
    public MailDto mailSend(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getEmail());
        message.setFrom(MailService.FROM_ADDRESS);
        message.setSubject("[ToeicLab]회원님의 이메일 인증번호입니다.");
        message.setText(mailDto.getEmailCheckToken());

        mailSender.send(message);
        return mailDto;
    }

    /**
     * 비밀번호 초기화시 사용자의 이메일로 비밀번호 초기화 token을 전송한다.
     * @param mailDto
     */
    public void resetPasswordMailSend(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getEmail());
        message.setFrom(MailService.FROM_ADDRESS);
        message.setSubject("[ToeicLab]회원님의 비밀번호 초기화 인증번호입니다.");
        message.setText(mailDto.getEmailCheckToken());

        mailSender.send(message);
        return;
    }

}
