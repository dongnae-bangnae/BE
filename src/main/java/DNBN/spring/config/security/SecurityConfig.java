package DNBN.spring.config.security;

import DNBN.spring.apiPayload.exception.handler.CustomAuthenticationEntryPoint;
import DNBN.spring.config.security.jwt.JwtAuthenticationFilter;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.service.OAuth2.CustomOAuth2UserService;
import DNBN.spring.service.OAuth2.CustomOidcUserService;
import DNBN.spring.service.OAuth2.OAuth2FailureHandler;
import DNBN.spring.service.OAuth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity // Spring Security 설정을 활성화시키는 역할 -> 따라서 우리가 직접 작성한 보안 설정이 Spring Security의 기본 설정보다 우선 적용되게 됨
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    CorsConfigurationSource corsConfigurationSource() { // CORS 설정
        CorsConfiguration config = new CorsConfiguration();

        // 동네방네 프론트, 백엔드 로컬, 운영 도메인 등 실제 사용하는 도메인 입력
        config.setAllowedOrigins(List.of(
//                "https://", // 프론트 도메인
//                "https://", // 백엔드 도메인
                "http://localhost:3000", // 로컬 프론트
                "http://localhost:8080" // 로컬 백엔드
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));  // JWT 토큰 읽기 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                                .requestMatchers("/admin/**").hasRole("ADMIN") // pm이 ADMIN역할 기능 필요 X
                                .anyRequest().authenticated()
                )
//                .csrf()
//                .disable()
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler) // 온보딩 분기 등 커스텀 성공 핸들러
                        .failureHandler(oAuth2FailureHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 입력 기능 없지만 추후 사용할지도...
//    @Bean
//    public PasswordEncoder passwordEncoder() { // 비밀번호를 암호화하여 저장하기 위해
//        return new BCryptPasswordEncoder();
//    }
}
