package DNBN.spring.config.security.jwt;

import DNBN.spring.config.properties.Constants;
import DNBN.spring.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider { // JWT 토큰을 생성하고, 검증하고, 인증 객체를 반환하는 역할을 수행

    private final JwtProperties jwtProperties;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String generateToken(Authentication authentication) { // 인증 정보를 받아서, JWT Access Token을 생성하고 반환
        String email = authentication.getName();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", authentication.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration().getAccess()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) { // 해당 JWT 토큰이 유효한지 검증
        // 파싱이 된다면 유효한 토큰이고, 토큰이 만료되었거나 (앞서 저희가 걸었던 4시간 제한을 넘어갔다거나) 혹은 위조, 형식 오류가 생기면 예외가 발생하여 false를 반환
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) { // JWT 토큰에서 인증 정보를 추출해서, Spring Security의 Authentication 객체로 변환
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        User principal = new User(email, "", Collections.singleton(() -> role));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTH_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

//    public Authentication extractAuthentication(HttpServletRequest request){ // HttpServletRequest 에서 토큰 값을 추출
//        String accessToken = resolveToken(request);
//        if(accessToken == null || !validateToken(accessToken)) {
//            throw new MemberHandler(ErrorStatus.INVALID_TOKEN);
//        }
//        return getAuthentication(accessToken); // getAuthentication 메소드를 이용해서 Spring Security의 Authentication 객체로 변환
//    }
}