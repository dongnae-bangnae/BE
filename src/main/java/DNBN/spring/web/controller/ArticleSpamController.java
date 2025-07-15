package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.ArticleSpamService.ArticleSpamQueryService;
import DNBN.spring.service.ArticleSpamService.ArticleSpamService;
import DNBN.spring.web.dto.SpamResponseDTO;
import DNBN.spring.web.dto.SpamStatusResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleSpamController {

    private final ArticleSpamService articleSpamService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ArticleSpamQueryService articleSpamQueryService;

    @Operation(
            summary = "광고 의심 신고 여부 조회",
            description = "해당 게시물에 대해 사용자가 광고 의심 신고를 눌렀는지 여부를 반환합니다."
    )
    @GetMapping("/{articleId}/spams/status")
    public ResponseEntity<ApiResponse<SpamStatusResponseDTO>> getSpamStatus(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        SpamStatusResponseDTO response = articleSpamQueryService.getSpamStatus(articleId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "광고 의심 신고 등록",
            description = "광고 의심 신고가 성공적으로 등록되었는지 확인합니다."
    )
    @PostMapping("/{articleId}/spams")
    public ResponseEntity<ApiResponse<SpamResponseDTO>> spamArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        SpamResponseDTO response = articleSpamService.spamArticle(articleId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "광고 의심 신고 취소",
            description = "광고 의심 신고가 성공적으로 취소되었는지 확인합니다."
    )
    @DeleteMapping("/{articleId}/spams")
    public ResponseEntity<ApiResponse<SpamResponseDTO>> unspamArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        SpamResponseDTO response = articleSpamService.unspamArticle(articleId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    private Long extractMemberIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String socialId = jwtTokenProvider.getSubjectFromToken(token);
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND))
                .getId();
    }
}
