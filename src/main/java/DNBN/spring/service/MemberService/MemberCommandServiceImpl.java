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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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
}
