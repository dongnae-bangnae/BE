package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.ArticleSpamService.ArticleSpamQueryService;
import DNBN.spring.service.ArticleSpamService.ArticleSpamService;
import DNBN.spring.web.dto.response.SpamResponseDTO;
import DNBN.spring.web.dto.response.SpamStatusResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleSpamController {

    private final ArticleSpamService articleSpamService;
    private final ArticleSpamQueryService articleSpamQueryService;

    @Operation(
            summary = "광고 의심 신고 여부 조회",
            description = "해당 게시물에 대해 사용자가 광고 의심 신고를 눌렀는지 여부를 반환합니다."
    )
    @GetMapping("/{articleId}/spams/status")
    public ResponseEntity<ApiResponse<SpamStatusResponseDTO>> getSpamStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        SpamStatusResponseDTO response = articleSpamQueryService.getSpamStatus(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.SPAM_STATUS_READ_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.SPAM_STATUS_READ_SUCCESS, response));
    }

    @Operation(
            summary = "광고 의심 신고 등록",
            description = "광고 의심 신고가 성공적으로 등록되었는지 확인합니다."
    )
    @PostMapping("/{articleId}/spams")
    public ResponseEntity<ApiResponse<SpamResponseDTO>> spamArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        SpamResponseDTO response = articleSpamService.spamArticle(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.SPAM_CREATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.SPAM_CREATE_SUCCESS, response));
    }

    @Operation(
            summary = "광고 의심 신고 취소",
            description = "광고 의심 신고가 성공적으로 취소되었는지 확인합니다."
    )
    @DeleteMapping("/{articleId}/spams")
    public ResponseEntity<ApiResponse<SpamResponseDTO>> unspamArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        SpamResponseDTO response = articleSpamService.unspamArticle(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.SPAM_DELETE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.SPAM_DELETE_SUCCESS, response));
    }
}
