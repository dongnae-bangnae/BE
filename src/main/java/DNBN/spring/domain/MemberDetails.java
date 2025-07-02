package DNBN.spring.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class MemberDetails implements UserDetails {
    private final Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예시: "ROLE_USER" 권한을 부여
        return Collections.singleton(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        // 소셜 로그인 기반이라면 비밀번호는 없음
        return "";
    }

    @Override
    public String getUsername() {
        // 여기서는 socialId를 식별자로 사용
        return member.getSocialId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
