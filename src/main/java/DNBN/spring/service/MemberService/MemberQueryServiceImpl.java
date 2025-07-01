package DNBN.spring.service.MemberService;

import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.MemberResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService{

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.MemberInfoDTO getMemberInfo(HttpServletRequest request){
        Authentication authentication = jwtTokenProvider.extractAuthentication(request); // 토큰을 파싱하고, Authentication 객체를 추출
        Long memberId = Long.parseLong(authentication.getName()); // 추출해낸 인증 객체(Authentication)을 통해 사용자 정보를 가져온다.

        Member member = memberRepository.findById(memberId) // MemberRepository 로부터 사용자 정보를 조회
                .orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberConverter.toMemberInfoDTO(member); // 정보 조회에 성공하면, 우리가 정의한 Response DTO인 MemberInfoDTO 로 반환
    }
}
