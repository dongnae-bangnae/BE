package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.converter.ArticleConverter;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.ArticleService.ArticleCommandService;
import DNBN.spring.service.ArticleService.ArticleQueryService;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleResponseDTO;
import DNBN.spring.web.dto.ArticleWithLocationRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
@SecurityRequirement(name = "JWT TOKEN")
public class ArticleController {
    private final ArticleCommandService articleCommandService;
    private final ArticleQueryService articleQueryService;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(
            summary = "게시물 생성",
            description = "새로운 게시물을 등록합니다. JWT 인증 필요.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<ArticleResponseDTO> createArticle(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart("request") @Valid ArticleRequestDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        Long memberId = memberDetails.getMember().getId();
        ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, dto, mainImage, imageFiles);
        ArticleResponseDTO response = ArticleConverter.toArticleResponseDTO(result.article, result.photos);
        return ApiResponse.of(SuccessStatus.ARTICLE_CREATE_SUCCESS, response);
    }

    @PostMapping(value = "/with-location", consumes = {"multipart/form-data"})
    @Operation(
            summary = "게시물 생성 (미등록 장소)",
            description = "핀(장소)이 등록되지 않은 경우, 위도/경도로 장소를 지정해 게시물을 등록합니다. JWT 인증 필요.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<ArticleResponseDTO> createArticleWithLocation(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart("request") @Valid ArticleWithLocationRequestDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        Long memberId = memberDetails.getMember().getId();
        ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, dto, mainImage, imageFiles);
        ArticleResponseDTO response = ArticleConverter.toArticleResponseDTO(result.article, result.photos);
        return ApiResponse.of(SuccessStatus.ARTICLE_CREATE_SUCCESS, response);
    }

    @DeleteMapping("/{articleId}")
    @Operation(
        summary = "게시물 삭제",
        description = "게시물을 삭제합니다. JWT 인증 필요.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<Void> deleteArticle(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long articleId
    ) {
        Long memberId = memberDetails.getMember().getId();
        articleCommandService.deleteArticle(memberId, articleId);
        return ApiResponse.of(SuccessStatus.ARTICLE_DELETE_SUCCESS, null);
    }

    @GetMapping("/{articleId}")
    @Operation(
        summary = "게시물 상세 조회",
        description = "게시물 상세페이지를 조회합니다.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<ArticleResponseDTO.ArticleDetailDTO> getArticleDetail(@PathVariable Long articleId) {
        ArticleResponseDTO.ArticleDetailDTO response = articleQueryService.getArticleDetail(articleId);
        return ApiResponse.of(SuccessStatus.ARTICLE_READ_SUCCESS, response);
    }

    @GetMapping
    @Operation(
        summary = "게시물 목록 조회",
        description = "게시물 목록을 조회합니다. JWT 인증 필요.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<List<ArticleResponseDTO.ArticleListItemDTO>> getArticleList(
            @AuthenticationPrincipal MemberDetails memberDetails,
            Long regionId,
            Long cursor,
            Long limit
    ) {
        Long memberId = memberDetails.getMember().getId();
        List<ArticleResponseDTO.ArticleListItemDTO> articles = articleQueryService.getArticleList(memberId, regionId, cursor, limit);
        return ApiResponse.of(SuccessStatus.ARTICLE_READ_SUCCESS, articles);
    }
}
