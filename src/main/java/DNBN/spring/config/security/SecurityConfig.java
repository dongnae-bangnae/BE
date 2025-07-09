package DNBN.spring.config.security;

import DNBN.spring.config.security.jwt.JwtAuthenticationFilter;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.service.OAuth2.CustomOAuth2UserService;
import DNBN.spring.service.OAuth2.OAuth2FailureHandler;
import DNBN.spring.service.OAuth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // Spring Security 설정을 활성화시키는 역할 -> 따라서 우리가 직접 작성한 보안 설정이 Spring Security의 기본 설정보다 우선 적용되게 됨
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    //    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests((requests) -> requests // HTTP 요청에 대한 접근 제어를 설정
//                        .requestMatchers("/", "/home", "/signup", "/members/signup", "/css/**").permitAll() // 특정 URL 패턴에 대한 접근 권한을 설정
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // 'ADMIN' 역할을 가진 사용자만 접근 가능하도록 제한
//                        .anyRequest().authenticated() // 그 외 모든 요청에 대해 인증을 요구
//                )
//                .formLogin((form) -> form
//                        .loginPage("/login") // 커스텀 로그인 페이지를 /login 경로로 지정
//                        .defaultSuccessUrl("/home", true) // 로그인 성공 시 /home으로 리다이렉트
//                        .permitAll() // 로그인 페이지는 모든 사용자가 접근 가능하도록 설정
//                )
//                .logout((logout) -> logout
//                        .logoutUrl("/logout") // /logout 경로로 로그아웃을 처리
//                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 /login?logout으로 리다이렉트
//                        .permitAll()
//                );
//
//        return http.build();
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/", "/members/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                                .requestMatchers("/admin/**").hasRole("ADMIN") // pm이 ADMIN역할 기능 필요 X
                                .anyRequest().authenticated()
                )
//                .csrf()
//                .disable()
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)  // 온보딩 분기 등 커스텀 성공 핸들러
                        .failureHandler(oAuth2FailureHandler)
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
