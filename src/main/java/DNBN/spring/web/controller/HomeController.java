package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.converter.ArticleConverter;
import DNBN.spring.domain.Article;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.service.ArticleService.ArticleQueryService;
import DNBN.spring.web.dto.response.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final ArticleQueryService articleQueryService;

    @GetMapping("/articles")
    @Operation(
            summary = "홈 화면 새 글 리스트 조회 API - JWT 인증 필요",
            description = "query string으로 페이지 번호를 입력하세요")
    @Parameters({
            @Parameter(name = "regionIds", description = "관심동네의 아이디 리스트, 예: regionIds=1,2,3"),
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", schema = @Schema(defaultValue = "1", minimum = "1"))
    })
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> getNewArticleList(
            @RequestParam List<Long> regionIds,
            // @ValidPage
            @RequestParam(name = "page", defaultValue = "1") Integer page
            ) {
        Page<Article> articlePreviewList = articleQueryService.getArticleListByRegion(regionIds, page);
        return ApiResponse.onSuccess(ArticleConverter.articlePreViewListDTO(articlePreviewList));
    }
}
