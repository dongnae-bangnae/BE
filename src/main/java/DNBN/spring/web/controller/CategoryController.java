package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.ArticleService.ArticleQueryService;
import DNBN.spring.service.CategoryService.CategoryQueryService;
import DNBN.spring.service.CategoryService.CategoryService;
import DNBN.spring.service.PlaceService.PlaceQueryService;
import DNBN.spring.web.dto.response.ArticleResponseDTO;
import DNBN.spring.web.dto.request.CategoryRequestDTO;
import DNBN.spring.web.dto.response.CategoryResponseDTO;
import DNBN.spring.web.dto.response.PlaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;
    private final PlaceQueryService placeQueryService;
    private final ArticleQueryService articleQueryService;

    @Operation(summary = "내 카테고리 목록 조회", description = "로그인한 사용자의 카테고리 목록을 반환합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getMyCategories(@AuthenticationPrincipal MemberDetails user) {
        Long memberId = user.getMember().getId();
        List<CategoryResponseDTO> categories = categoryQueryService.getMyCategories(memberId);
        return ResponseEntity.status(SuccessStatus.CATEGORY_LIST_READ_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_LIST_READ_SUCCESS, categories));
    }

    @PostMapping
    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 추가합니다.")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> addCategory(
            @AuthenticationPrincipal MemberDetails user,
            @RequestBody @Valid CategoryRequestDTO dto) {
        Long memberId = user.getMember().getId();
        CategoryResponseDTO response = categoryService.create(memberId, dto);
        return ResponseEntity.status(SuccessStatus.CATEGORY_CREATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_CREATE_SUCCESS, response));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "카테고리 이름 또는 색상을 수정합니다.")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @AuthenticationPrincipal MemberDetails user,
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequestDTO dto) {
        Long memberId = user.getMember().getId();
        CategoryResponseDTO response = categoryService.update(memberId, categoryId, dto);
        return ResponseEntity.status(SuccessStatus.CATEGORY_UPDATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_UPDATE_SUCCESS, response));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "해당 카테고리를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal MemberDetails user,
            @PathVariable Long categoryId) {
        Long memberId = user.getMember().getId();
        categoryService.delete(memberId, categoryId);
        return ResponseEntity.status(SuccessStatus.CATEGORY_DELETE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_DELETE_SUCCESS, null));
    }

    @Operation(summary = "카테고리에 저장된 장소 목록 조회", description = "해당 카테고리에 저장된 장소들을 커서 기반 페이징으로 반환합니다.")
    @GetMapping("/{categoryId}/places")
    public ResponseEntity<ApiResponse<PlaceResponseDTO.SavedPlaceListDTO>> getPlacesByCategory(
            @PathVariable Long categoryId,

            @Parameter(
                    name = "cursor",
                    description = "다음 페이지 요청 시 기준이 되는 save_place PK (default: null)",
                    schema = @Schema(type = "integer", format = "int64", defaultValue = "null")
            )
            @RequestParam(required = false) Long cursor,

            @Parameter(
                    name = "limit",
                    description = "최대 응답 개수 (default: 20)",
                    schema = @Schema(type = "integer", format = "int64", defaultValue = "20"),
                    example = "20"
            )
            @RequestParam(defaultValue = "20") Long limit,

            @AuthenticationPrincipal MemberDetails user
    ) {
        Long memberId = user.getMember().getId();
        PlaceResponseDTO.SavedPlaceListDTO result = placeQueryService.getSavedPlaces(categoryId, memberId, cursor, limit);
        return ResponseEntity.status(SuccessStatus.CATEGORY_PLACE_LIST_READ_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_PLACE_LIST_READ_SUCCESS, result));
    }

    @Operation(summary = "카테고리에 작성된 게시물 목록 조회", description = "해당 카테고리에 작성된 게시물들을 반환합니다.")
    @GetMapping("/{categoryId}/articles")
    public ResponseEntity<ApiResponse<ArticleResponseDTO.ArticleListDTO>> getArticlesByCategory(
            @PathVariable Long categoryId,

            @Parameter(
                    name = "cursor",
                    description = "다음 페이지 기준이 되는 커서 articleId (default: null)",
                    schema = @Schema(type = "integer", format = "int64", defaultValue = "null")
            )
            @RequestParam(required = false) Long cursor,

            @Parameter(
                    name = "limit",
                    description = "최대 응답 개수 (default: 20)",
                    schema = @Schema(type = "integer", format = "int64", defaultValue = "20"),
                    example = "20"
            )
            @RequestParam(defaultValue = "20") Long limit,

            @AuthenticationPrincipal MemberDetails user) {

        Long memberId = user.getMember().getId();
        ArticleResponseDTO.ArticleListDTO result = articleQueryService.getArticlesByCategory(categoryId, memberId, cursor, limit);
        return ResponseEntity.status(SuccessStatus.CATEGORY_ARTICLE_LIST_READ_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CATEGORY_ARTICLE_LIST_READ_SUCCESS, result));
    }
}