package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.PlaceService.PlaceCommandService;
import DNBN.spring.service.PlaceService.PlaceQueryService;
import DNBN.spring.web.dto.PlaceRequestDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceRestController {

    private final PlaceCommandService placeCommandService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PlaceQueryService placeQueryService;

    @Operation(summary = "장소 카테고리에 저장", description = "장소를 사용자의 카테고리에 저장합니다.")
    @PostMapping("/{placeId}/categories")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.SavePlaceResultDTO>> savePlaceToCategory(
            HttpServletRequest request,
            @PathVariable Long placeId,
            @RequestBody @Valid PlaceRequestDTO.SavePlaceDTO dto) {

        Long memberId = extractMemberIdFromToken(request);
        PlaceResponseDTO.SavePlaceResultDTO response = placeCommandService.savePlaceToCategory(memberId, placeId, dto);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "카테고리에 저장된 장소 목록 조회", description = "해당 카테고리에 저장된 장소들을 조회합니다.")
    @GetMapping("/categories/{categoryId}/places")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.SavedPlaceListDTO>> getSavedPlacesByCategory(
            @PathVariable Long categoryId,

            @Parameter(name = "cursor", description = "다음 페이지 요청 시 기준이 되는 커서 ID (save_place PK) (default: null)", example = "0")
            @RequestParam(required = false) Long cursor,

            @Parameter(name = "limit", description = "최대 응답 개수 (default: 20)", example = "20")
            @RequestParam(defaultValue = "20") Long limit,

            HttpServletRequest request
    ) {
        Long memberId = extractMemberIdFromToken(request);
        PlaceResponseDTO.SavedPlaceListDTO response = placeQueryService.getSavedPlaces(categoryId, memberId, cursor, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    private Long extractMemberIdFromToken(HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String socialId = jwtTokenProvider.getSubjectFromToken(token);
        Member member = memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return member.getId();
    }
}
