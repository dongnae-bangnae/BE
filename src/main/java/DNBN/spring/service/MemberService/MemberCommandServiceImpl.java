package DNBN.spring.service.MemberService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.converter.MemberConverter;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.Uuid;
import DNBN.spring.domain.mapping.LikeRegion;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.ProfileImageRepository.ProfileImageRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.repository.UuidRepository.UuidRepository;
import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final LikeRegionRepository likeRegionRepository;
    private final RegionRepository regionRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final ProfileImageRepository profileImageRepository;

    @Override
    @Transactional
    public Member onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request, MultipartFile profileImage) {
        // 기존 회원이 존재하는지를 따짐
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 온보딩 완료 여부 체크
        if (member.isOnboardingCompleted()) {
            throw new MemberHandler(ErrorStatus.ONBOARDING_NOT_COMPLETED);
        }

        // 닉네임 null 혹은 빈 문자열 체크
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_NOT_EXIST);
        }

        // 좋아하는 동네 개수 최소 1개 ~ 최대 3개
        int chosenRegionCount = request.getChosenRegionIds() == null ? 0 : request.getChosenRegionIds().size();
        if (chosenRegionCount < 1 || chosenRegionCount > 3) {
            throw new MemberHandler(ErrorStatus.INVALID_REGION_COUNT);
        }

        // 선택한 지역들이 모두 존재하는지 확인
        for (Long regionId : request.getChosenRegionIds()) {
            boolean exists = regionRepository.existsById(regionId);
            if (!exists) {
                throw new MemberHandler(ErrorStatus.REGION_NOT_FOUND);
            }
        }

        member.setNickname(request.getNickname());

        if (profileImage != null && !profileImage.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String pictureUrl = s3Manager.uploadFile(s3Manager.generateMemberKeyName(savedUuid), profileImage);

            profileImageRepository.save(MemberConverter.toProfileImage(pictureUrl, member));
        }

        likeRegionRepository.saveAll( // 좋아하는 동네 연결
                request.getChosenRegionIds().stream() // 프론트에서 넘겨준 값
                        .map(regionId -> LikeRegion.of(member, findRegion(regionId))) // 각 regionId에 대해 LikeRegion.of(member, region)를 호출해서 LikeRegion 객체들 생성
                        .collect(Collectors.toList()) // 방금 만든 LikeRegion 객체들을 한 번에 DB에 저장
        );

        member.setOnboardingCompleted(true);

        return member;
//        return memberRepository.save(member);
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

    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 멤버 테이블에서 멤버 삭제
        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public void changeMemberNickname(Long memberId, String newNickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!member.isOnboardingCompleted()) {
            throw new MemberHandler(ErrorStatus.ONBOARDING_NOT_COMPLETED);
        }

        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_NOT_EXIST);
        }

//        member.setNickname(newNickname);
        member.updateNickname(newNickname); // 도메인 주도 설계(Domain-Driven Design) 원칙에 부합하도록
    }

    @Override
    @Transactional
    public MemberResponseDTO.ChosenRegionsDTO updateRegions(Long memberId, List<Long> regionIds) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (regionIds.size() < 1 || regionIds.size() > 3) {
            throw new MemberHandler(ErrorStatus.INVALID_REGION_COUNT);
        }

        // 모든 지역 ID가 존재하는지 확인
        List<Region> regions = regionIds.stream()
                .map(id -> regionRepository.findById(id)
                        .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND)))
                .toList();

        // 기존 관심 동네 삭제
        likeRegionRepository.deleteByMember(member);

        // 새 관심 동네 저장
        List<LikeRegion> newLikeRegions = regions.stream()
                .map(region -> LikeRegion.of(member, region))
                .toList();
        likeRegionRepository.saveAll(newLikeRegions);

        return MemberResponseDTO.ChosenRegionsDTO.builder()
                .chosenRegions(regions.stream()
                        .map(region -> MemberResponseDTO.ChosenRegionsDTO.RegionInfo.builder()
                                .regionId(region.getId())
                                .district(region.getDistrict())
                                .build())
                        .toList())
                .build();
    }

}
