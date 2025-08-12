package DNBN.spring.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class CsrfCookieConfig {
    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        // setCookieCustomizer로 쿠키 속성 세밀하게 지정
        repository.setCookieCustomizer(builder ->
                builder
                        .httpOnly(false) // CSRF 쿠키는 JS에서 읽을 수 있도록 false
                        .secure(true) // HTTPS 환경에서 true
                        .path("/")
                        .domain("dnbn.site")
                        .maxAge(60 * 60 * 4) // 4시간
                        .sameSite("None")
        );
        return repository;
    }
}
