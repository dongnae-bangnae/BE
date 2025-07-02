package DNBN.spring.config.security;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        Member member = memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 소셜 ID의 유저가 존재하지 않습니다: " + socialId));

//        return org.springframework.security.core.userdetails.User
//                .withUsername(member.getSocialId())
//                .password("")
//                .roles("USER")
//                .build();
        return new MemberDetails(member);
    }
}
