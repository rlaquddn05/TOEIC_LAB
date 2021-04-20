package toeicLab.toeicLab.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import toeicLab.toeicLab.configuration.oauth.PrincipalOauth2UserService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final PrincipalOauth2UserService principalOauth2UserService;

    /**
     * 로그인 유지 기능입니다.
     * @return repository
     */
    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

    /**
     * 사용자에게 로그인시 보여주는 페이지와 로그아웃 상태에서 볼 수 있는 페이지를 나타냅니다.
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/common.css","/css/**","/images/**","/js/**","**/*.ico")
                .permitAll()
                .mvcMatchers("/index",
                        "/",
                        "/select_test",
                        "/login",
                        "/signup",
                        "/my_page",
                        "/send_find_id_link",
                        "/send_reset_password_link",
                        "/my_review_note",
                        "/result_sheet",
                        "/my_studygroup_list",
                        "/my_studygroup_detail",
                        "/practice_test/**",
                        "/signup/**",
                        "/reset/checkTokens",
                        "/notify_password",
                        "/reset_password",
                        "/notify_password2",
                        "/detail/**",
                        "/update/**",
                        "/create_meeting/**",
                        "/forum/**",
                        "/forum_upload/**",
                        "/bulletin",
                        "/popup_dictionary",
                        "/popup_dictionary_find/**",
                        "/add_word_list/**",
                        "/add_word_list",
                        "/my_vocabulary_list",
                        "/delete_word",
                        "/readText",
                        "/forum_upload",
                        "/spk_sheet",
                        "/spk_confirm_answer",
                        "/spk_answer_sheet"
                        )
                .permitAll()

                .mvcMatchers(HttpMethod.GET, "/item/*")
                .permitAll()
                .anyRequest().authenticated();

        http.logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/index");

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .defaultSuccessUrl("/index")
                .usernameParameter("userId")
                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
    }
}

