package DNBN.spring.service.MemberService;

import DNBN.spring.aop.annotation.ValidateS3ImageUpload;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
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
    public Member onboardingMember(Long memberId) {
        // 기존 회원이 존재하는지를 따짐
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        boolean hasNickname = member.getNickname() != null && !member.getNickname().isBlank();
        boolean hasRegions = member.getLikeRegionList() != null && !member.getLikeRegionList().isEmpty();

        if (hasNickname && hasRegions) {
            member.setOnboardingCompleted(true); // @Transactional에 의해 메서드 종료 시 변경 사항이 DB에 자동 반영
        } // 조건이 충족되지 않으면, member의 isOnboardingCompleted는 기본값(false)인 채로 유지

        /*
        if (member.getNickname() == null || member.getNickname().isBlank()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_NOT_EXIST);
        }

        // 좋아하는 동네 개수 최소 1개 ~ 최대 3개
        if (member.getLikeRegionList() == null || member.getLikeRegionList().isEmpty()) {
            throw new MemberHandler(ErrorStatus.INVALID_REGION_COUNT);
        }

        member.setOnboardingCompleted(true);
        */
        return member;
    }

    @Override
    public void logout(HttpServletResponse response, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // JWT를 로컬(localStorage, 쿠키 등)에서 직접 제거해야 로그아웃
        ResponseCookie deleteAccessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain("dnbn.site")
                .maxAge(0)
                .sameSite("None")
                .build();

        // refreshToken 쿠키 삭제 (즉시 만료 설정)
        ResponseCookie deleteRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // HTTPS 환경이라면 true
                .path("/")
                .domain("dnbn.site") // 운영 도메인과 맞춰서 설정
                .maxAge(0) // 즉시 만료
                .sameSite("None")
                .build();

        // CSRF 토큰 쿠키 삭제
        ResponseCookie deleteCsrfCookie = ResponseCookie.from("XSRF-TOKEN", "")
                .httpOnly(false) // 일반 쿠키라 false
                .secure(true)
                .path("/")
                .domain("dnbn.site") // 테스트 시 주석처리
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", deleteAccessTokenCookie.toString());
        response.addHeader("Set-Cookie", deleteRefreshTokenCookie.toString());
        response.addHeader("Set-Cookie", deleteCsrfCookie.toString());

        log.info("사용자 {} 로그아웃 처리 및 JWT와 CSRF 쿠키 삭제 완료", member.getId());
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
    public MemberResponseDTO.NicknameUpdateResultDTO updateMemberNickname(Long memberId, String newNickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

//        if (!member.isOnboardingCompleted()) {
//            throw new MemberHandler(ErrorStatus.ONBOARDING_NOT_COMPLETED);
//        }

        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_NOT_EXIST);
        }

        validateNicknameDuplicate(newNickname);

//        member.setNickname(newNickname);
        member.updateNickname(newNickname); // 도메인 주도 설계(Domain-Driven Design) 원칙에 부합하도록

        return MemberResponseDTO.NicknameUpdateResultDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
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

    @Override
    @Transactional
    @ValidateS3ImageUpload
    public MemberResponseDTO.ProfileImageUpdateResultDTO updateProfileImage(Long memberId, MultipartFile profileImage) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

//        if (profileImage == null || profileImage.isEmpty()) {
//            throw new MemberHandler(ErrorStatus._BAD_REQUEST);
//        }

        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            // 파일 형식 검사
            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new MemberHandler(ErrorStatus.INVALID_IMAGE_TYPE);
            }

            // 파일 용량 제한 (10MB)
            long maxFileSize = 10 * 1024 * 1024;
            if (profileImage.getSize() > maxFileSize) {
                throw new MemberHandler(ErrorStatus.IMAGE_FILE_TOO_LARGE);
            }

            // UUID 생성 후 저장
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());

            // S3 업로드
            profileImageUrl = s3Manager.uploadFile(s3Manager.generateMemberKeyName(savedUuid), profileImage);

            if (member.getProfileImage() != null) {
                // 기존 이미지의 키 추출 및 삭제
                String oldKey = s3Manager.extractS3KeyFromUrl(member.getProfileImage().getImageUrl());
                s3Manager.deleteFile(oldKey);

                // update: 기존 엔티티에 새로운 URL만 set
                member.getProfileImage().updateImageUrl(profileImageUrl);
            } else {
                // 새 이미지 insert
                profileImageRepository.save(MemberConverter.toProfileImage(profileImageUrl, member));
            }
        } else { // 이미지 없을 때는 기존 URL 유지
            profileImageUrl = member.getProfileImage() != null
                    ? member.getProfileImage().getImageUrl()
                    : null;
        }

        return MemberResponseDTO.ProfileImageUpdateResultDTO.builder()
                .profileImageUrl(profileImageUrl)
                .build();
    }

    private void validateNicknameDuplicate(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            throw new MemberHandler(ErrorStatus.NICKNAME_DUPLICATE);
        }
    }
}
