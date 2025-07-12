package DNBN.spring.service.OAuth2;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.enums.Provider;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final MemberRepository memberRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oidcUser.getAttributes());
        log.info("🌐 provider: {}, 🐤 attributes: {}, accessToken = {}", provider, oidcUser.getAttributes(), userRequest.getAccessToken().getTokenValue());

        if (userInfo.getSocialId() == null) {
            // 커스텀 예외 던지기
            throw new MemberHandler(ErrorStatus.INVALID_SOCIAL_TOKEN);
        }

        String checkSocialId = provider.toLowerCase() + "_" + userInfo.getSocialId();
        log.info("🔑 최종 socialId: {}", checkSocialId);

        Member member = memberRepository.findBySocialId(checkSocialId)
                .orElseGet(() -> saveNewMember(userInfo, provider));

        return new CustomOAuth2User(member, oidcUser.getAttributes());
    }

    private Member saveNewMember(OAuth2UserInfo userInfo, String provider) {
        log.info("🆕 {} 신규 유저로 저장 시도", provider);
        String socialId = provider.toLowerCase() + "_" + userInfo.getSocialId();
        Member member = Member.builder()
                .socialId(socialId)
                .provider(Provider.from(provider))
                .isOnboardingCompleted(false)
                .build();
        return memberRepository.save(member);
    }
}
