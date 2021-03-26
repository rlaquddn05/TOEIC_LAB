package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import toeicLab.toeicLab.service.MemberService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    private final MemberService memberService;

    @Bean // 로그인 유지 (persistence_logins) 레파지토리 빈으로 등록
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/common.css","/css/**","/images/**","/js/**","**/*.ico")
                .permitAll()

                .mvcMatchers("/",
                        "/create_meeting",
                        "/lc_sheet",
                        "/rc_sheet",
                        "/rc_answer_sheet",
                        "/select_test",
                        "/login",
                        "/signup",
                        "/my_page",
                        "/send_find_id_link",
                        "/send_reset_password_link",
                        "/my_review_note",
                        "/spk_sheet",
                        "/spk_confirm_answer",
                        "/result_sheet",
                        "/lc_answer_sheet",
                        "/apply_studygroup",
                        "/my_page",
                        "/spk_answer_sheet",
                        "/my_studygroup_list",
                        "/my_studygroup_detail",
                        "/practice_test/**",
                        "/signup/**",
                        "/reset/**")
                .permitAll()

                .mvcMatchers(HttpMethod.GET, "/item/*")
                .permitAll()
                .anyRequest().authenticated();

        // 로그인 유지 기능 추가
        http.rememberMe()
                .userDetailsService(memberService);

        // 로그아웃 기능 추가
        http.logout()
                .logoutUrl("/logout") // 이 경로는 이미 default
                .invalidateHttpSession(true) // 로그아웃하면 session 이 갱신됨
                .logoutSuccessUrl("/"); // 로그아웃이 완료된 후 어디로 갈 지

        // 로그인 기능 추가
        http.formLogin()
                .loginPage("/login")
                .permitAll();
    }

}

