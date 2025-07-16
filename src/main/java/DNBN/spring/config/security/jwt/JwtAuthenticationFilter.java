package DNBN.spring.config.security.jwt;

import DNBN.spring.config.properties.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 필터 역할: 클라이언트가 서버에게 전달되기 전에 인증 과정

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, // HttpServletRequest로 받아온 요청 객체에서 순수한 토큰을 추출
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if(StringUtils.hasText(token) &&
                jwtTokenProvider.validateToken(token) &&
                jwtTokenProvider.isAccessToken(token)) { // 순수한 토큰 검증 과정을 통과하면,
            Authentication authentication = jwtTokenProvider.getAuthentication(token); // 인증 객체를 만들고,
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증을 등록
        }
        filterChain.doFilter(request, response); // 다음 필터, 컨트롤러 등에 요청을 넘겨줌
    }

    private String resolveToken(HttpServletRequest request) { // 순수 토큰을 반환
        String bearerToken = request.getHeader(Constants.AUTH_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }
}