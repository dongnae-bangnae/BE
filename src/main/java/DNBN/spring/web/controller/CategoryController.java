package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.CategoryService.CategoryQueryService;
import DNBN.spring.service.CategoryService.CategoryService;
import DNBN.spring.service.PlaceService.PlaceQueryService;
import DNBN.spring.web.dto.CategoryRequestDTO;
import DNBN.spring.web.dto.CategoryResponseDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PlaceQueryService placeQueryService;

    @Operation(summary = "내 카테고리 목록 조회", description = "로그인한 사용자의 카테고리 목록을 반환합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getMyCategories(HttpServletRequest request) {
        Long memberId = extractMemberIdFromToken(request);
        List<CategoryResponseDTO> categories = categoryQueryService.getMyCategories(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(categories));
    }

    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> addCategory(
            HttpServletRequest request,
            @RequestBody @Valid CategoryRequestDTO dto) {
        Long memberId = extractMemberIdFromToken(request);
        CategoryResponseDTO response = categoryService.create(memberId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccess(response));

    }

    @Operation(summary = "카테고리 수정", description = "카테고리 이름 또는 색상을 수정합니다.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            HttpServletRequest request,
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequestDTO dto) {
        Long memberId = extractMemberIdFromToken(request);
        CategoryResponseDTO response = categoryService.update(memberId, categoryId, dto);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "카테고리 삭제", description = "해당 카테고리를 삭제합니다.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            HttpServletRequest request,
            @PathVariable Long categoryId) {
        Long memberId = extractMemberIdFromToken(request);
        categoryService.delete(memberId, categoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary     = "카테고리에 저장된 장소 목록 조회", description = "해당 카테고리에 저장된 장소들을 커서 기반 페이징으로 반환합니다.")
    @GetMapping("/{categoryId}/places")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.SavedPlaceListDTO>> getPlacesByCategory(
            @PathVariable Long categoryId,

            @Parameter(
                    name        = "cursor",
                    description = "다음 페이지 요청 시 기준이 되는 save_place PK (default: null)",
                    schema      = @Schema(type="integer", format="int64", defaultValue="null")
            )
            @RequestParam(required = false) Long cursor,

            @Parameter(
                    name        = "limit",
                    description = "최대 응답 개수 (default: 20)",
                    schema      = @Schema(type="integer", format="int64", defaultValue="20"),
                    example     = "20"
            )
            @RequestParam(defaultValue = "20") Long limit,

            HttpServletRequest request
    ) {
        Long memberId = extractMemberIdFromToken(request);
        PlaceResponseDTO.SavedPlaceListDTO result =
                placeQueryService.getSavedPlaces(categoryId, memberId, cursor, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
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
