package DNBN.spring.service.OAuth2;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.enums.Provider;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // DefaultOAuth2UserService는 Spring Security에 기본적으로 제공되는 클래스
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // 소셜 API에서 사용자 정보 가져오기

        // 2. provider 정보 (kakao, google, naver)
        String provider = userRequest.getClientRegistration().getRegistrationId(); // "kakao", "google" 등
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        log.info("🌐 provider: {}, 🐤 attributes: {}, accessToken = {}", provider, oAuth2User.getAttributes(), userRequest.getAccessToken().getTokenValue());

        if (userInfo.getSocialId() == null) {
//            throw new OAuth2AuthenticationException("소셜 로그인 ID가 없습니다.");
            throw new MemberHandler(ErrorStatus.INVALID_SOCIAL_TOKEN);
        }

        // 3. 회원 조회 or 신규 회원 등록
        String checkSocialId = provider.toLowerCase() + "_" + userInfo.getSocialId();
        log.info("🔑 최종 socialId: {}", checkSocialId);

//        Optional<Member> existing = memberRepository.findBySocialId(socialId);
//        log.info("🔎 DB에 기존 회원 존재 여부: {}", existing.isPresent());
//        Member member;
//        if (existing.isPresent()) {
//            member = existing.get();
//            log.info("회원 로그인 성공: {}", SuccessStatus.MEMBER_LOGIN_SUCCESS.getMessage());
//            // 필요시 기존 회원 업데이트 로직 추가 가능
//        } else {
//            member = saveNewMember(userInfo, provider);
//            log.info("신규 회원 온보딩 필요: {}", SuccessStatus.MEMBER_NEEDS_ONBOARDING.getMessage());
//        }
        Member member = memberRepository.findBySocialId(checkSocialId)
                .orElseGet(() -> saveNewMember(userInfo, provider));

        // 4. 반환할 OAuth2User 구현체 (권한 부여용)
        return new CustomOAuth2User(member, oAuth2User.getAttributes());
//        return super.loadUser(userRequest);
    }

    private Member saveNewMember(OAuth2UserInfo userInfo, String provider) {
        log.info("🆕 {} 신규 유저로 저장 시도", provider);
        String socialId = provider.toLowerCase() + "_" + userInfo.getSocialId(); // kakao_12345
        Member member = Member.builder()
//                .socialId(userInfo.getSocialId())
                .socialId(socialId)
                .provider(Provider.from(provider))
                .isOnboardingCompleted(false)
                .build();
        return memberRepository.save(member);
    }
}