package DNBN.spring.service.MemberService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
import DNBN.spring.converter.MemberConverter;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.mapping.LikePlace;
import DNBN.spring.repository.LikePlaceRepository.LikePlaceRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final LikePlaceRepository likePlaceRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional
    public MemberResponseDTO.OnboardingResultDTO onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request) {
        // 기존 회원이 존재하는지를 따짐
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        // 닉네임이 비었는지
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_NOT_EXIST);
        }
        // 좋아하는 동네 개수 최소 1개 ~ 최대 3개
        int chosenRegionCount = request.getChosenRegionIds() == null ? 0 : request.getChosenRegionIds().size();
        if (chosenRegionCount < 1 || chosenRegionCount > 3) {
            throw new MemberHandler(ErrorStatus.INVALID_REGION_COUNT);
        }

        member.updateOnboardingInfo(
                request.getNickname(),
                request.getProfileImageUrl()
        );

        likePlaceRepository.saveAll( // 좋아하는 동네 연결
                request.getChosenRegionIds().stream()
                        .map(regionId -> LikePlace.of(member, findRegion(regionId)))
                        .collect(Collectors.toList())
        );

        return MemberConverter.toOnboardingResultDTO(member);
    }

    private Region findRegion(Long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));
    }

    @Override
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // JWT를 로컬(localStorage, 쿠키 등)에서 직접 제거해야 로그아웃

        log.info("사용자 {} 로그아웃 처리 완료", member.getId());
    }
}
