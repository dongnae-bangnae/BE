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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceRestController {

    private final PlaceCommandService placeCommandService;
    private final PlaceQueryService placeQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

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

    @Operation(
            summary      = "지도 화면 내 핀 등록된 장소 조회",
            description  = "현재 보고 있는 지도의 경계(위도/경도 범위) 안에 등록된 모든 장소를 반환합니다."
    )
    @GetMapping("/map")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.MapPlacesResultDTO>> getPlacesInMap(
            HttpServletRequest request,

            @Parameter(
                    name        = "latMin",
                    description = "조회할 영역의 최소 위도 (소수점 5자리까지 입력)",
                    schema      = @Schema(type="number", format="double", example="37.50000")
            )
            @RequestParam @Digits(integer = 3, fraction = 5, message = "소수점 5자리까지 입력 가능합니다")
            Double latMin,

            @Parameter(
                    name        = "latMax",
                    description = "조회할 영역의 최대 위도 (소수점 5자리까지 입력)",
                    schema      = @Schema(type="number", format="double", example="37.60000")
            )
            @RequestParam @Digits(integer = 3, fraction = 5)
            Double latMax,

            @Parameter(
                    name        = "lngMin",
                    description = "조회할 영역의 최소 경도 (소수점 5자리까지 입력)",
                    schema      = @Schema(type="number", format="double", example="126.90000")
            )
            @RequestParam @Digits(integer = 3, fraction = 5)
            Double lngMin,

            @Parameter(
                    name        = "lngMax",
                    description = "조회할 영역의 최대 경도 (소수점 5자리까지 입력)",
                    schema      = @Schema(type="number", format="double", example="126.98000")
            )
            @RequestParam @Digits(integer = 3, fraction = 5)
            Double lngMax
    ) {
        placeQueryService.getPlacesInMapBounds(latMin, latMax, lngMin, lngMax);
        return ResponseEntity.ok(ApiResponse.onSuccess(
                placeQueryService.getPlacesInMapBounds(latMin, latMax, lngMin, lngMax)
        ));
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
