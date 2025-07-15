package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.ArticleLikeService.ArticleLikeService;
import DNBN.spring.web.dto.AuthResponseDTO;
import DNBN.spring.web.dto.LikeResponseDTO;
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
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;


    @Operation(
            summary = "좋아요 등록",
            description = "좋아요가 성공적으로 등록되었습니다."
    )
    @PostMapping("/{articleId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> likeArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        LikeResponseDTO response = articleLikeService.likeArticle(articleId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "좋아요 취소",
            description = "좋아요가 성공적으로 취소되었습니다."
    )
    @DeleteMapping("/{articleId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> unlikeArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        LikeResponseDTO response = articleLikeService.unlikeArticle(articleId, memberId);
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
