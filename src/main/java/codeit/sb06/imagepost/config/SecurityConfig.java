package codeit.sb06.imagepost.config;

import codeit.sb06.imagepost.entity.Member;
import codeit.sb06.imagepost.entity.Role;
import codeit.sb06.imagepost.repository.MemberRepository;
import codeit.sb06.imagepost.security.ApiInvalidSessionStrategy;
import codeit.sb06.imagepost.security.ApiSessionExpiredStrategy;
import codeit.sb06.imagepost.security.RestAuthenticationFailureHandler;
import codeit.sb06.imagepost.security.RestAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .successHandler(new RestAuthenticationSuccessHandler())
                        .failureHandler(new RestAuthenticationFailureHandler())
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionStrategy(new ApiInvalidSessionStrategy())
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry())
                                .expiredSessionStrategy(new ApiSessionExpiredStrategy())
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((req,res,auth) -> res.setStatus(200))
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(memberRepository.findByUsername("user").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("user")
                        .password(passwordEncoder.encode("1234"))
                        .role(Role.USER)
                        .build());
            }
            if(memberRepository.findByUsername("admin").isEmpty()) {
                memberRepository.save(
                  Member.builder()
                          .username("admin")
                          .password("1234")
                          .role(Role.ADMIN)
                          .build());
            }
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
