package DNBN.spring.service.AuthService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import DNBN.spring.domain.MemberDetails;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponseDTO.ReissueTokenResponseDTO reissue(String refreshToken) {
        // 1. RefreshToken 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new MemberHandler(ErrorStatus.INVALID_JWT_REFRESH_TOKEN);
        }

        // 2. RefreshToken에서 subject(socialId) 추출
        String socialId = jwtTokenProvider.getSubjectFromToken(refreshToken);

        // 3. DB에서 해당 유저 조회 (예외 처리 포함)
        Member member = memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        MemberDetails memberDetails = new MemberDetails(member);

        // 4. AccessToken 새로 생성 (Authentication 객체 없이도 직접 생성 가능)
//        String newAccessToken = jwtTokenProvider.generateAccessTokenFromSocialId(socialId);
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(
//                        socialId,
//                        null,
//                        member.getAuthorities()
                        memberDetails.getUsername(), // socialId
                        null,
                        memberDetails.getAuthorities()
                )
        );

        // 5. 응답 DTO로 포장하여 반환
        return AuthResponseDTO.ReissueTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
