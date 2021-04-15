package toeicLab.toeicLab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfiguration {
    /**
     * 사용자가 입력한 비밀번호를  DB에 저장할 때 암호화하는 기능입니다.
     * @return PasswordEncoderFactories.createDelegatingPasswordEncoder()
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
