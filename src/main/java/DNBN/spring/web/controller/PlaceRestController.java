package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.PlaceService.PlaceCommandService;
import DNBN.spring.web.dto.PlaceRequestDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "장소를 카테고리에 저장", description = "장소를 사용자의 카테고리에 저장합니다.")
    @PostMapping("/{placeId}/categories")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.SavePlaceResultDTO>> savePlaceToCategory(
            HttpServletRequest request,
            @PathVariable Long placeId,
            @RequestBody @Valid PlaceRequestDTO.SavePlaceDTO dto) {

        Long memberId = extractMemberIdFromToken(request);
        PlaceResponseDTO.SavePlaceResultDTO response = placeCommandService.savePlaceToCategory(memberId, placeId, dto);
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
