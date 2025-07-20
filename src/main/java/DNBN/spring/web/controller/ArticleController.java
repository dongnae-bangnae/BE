package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.converter.ArticleConverter;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.ArticleService.ArticleCommandService;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return ApiResponse.onSuccess(response);
    }
}
