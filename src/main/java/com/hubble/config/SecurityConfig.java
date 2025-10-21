package com.hubble.config;

import com.hubble.security.jwt.JwtAuthenticationFilter;
import com.hubble.security.jwt.JwtTokenProvider;
import com.hubble.security.oauth.CustomOAuth2UserService;
import com.hubble.security.oauth.OAuth2FailureHandler;
import com.hubble.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;
    private final JwtTokenProvider jwt;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트
                        .requestMatchers(
                            "/auth/**", 
                            "/actuator/health",
                            "/uploads/**",           // 업로드된 파일 접근
                            "/notes/public/**",      // 공개 노트 피드
                            "/notes/{noteId}",       // 노트 상세 (권한은 따로 체크)
                            "/users/{userId}",       // 프로필 조회
                            "/search/**",            // 검색
                            "/interests"             // 관심사 목록
                        ).permitAll()
                        // 인증 필요
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(oAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwt), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
