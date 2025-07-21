package DNBN.spring.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import DNBN.spring.domain.MemberDetails;

public class SecurityUtils {
    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자 인증 정보가 존재하지 않습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof MemberDetails memberDetails) {
            return memberDetails.getMemberId();
        }

        throw new IllegalStateException("인증 정보가 MemberDetails 타입이 아닙니다.");
    }
}
