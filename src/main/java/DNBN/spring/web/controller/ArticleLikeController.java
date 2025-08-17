package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.ArticleLikeService.ArticleLikeQueryService;
import DNBN.spring.service.ArticleLikeService.ArticleLikeService;
import DNBN.spring.web.dto.response.LikeResponseDTO;
import DNBN.spring.web.dto.response.LikeStatusResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ArticleLikeQueryService articleLikeQueryService;

    @Operation(
            summary = "좋아요 여부 조회",
            description = "해당 게시물에 대해 사용자가 좋아요를 눌렀는지 여부를 반환합니다."
    )
    @GetMapping("/{articleId}/likes/status")
    public ResponseEntity<ApiResponse<LikeStatusResponseDTO>> getLikeStatus(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        LikeStatusResponseDTO response = articleLikeQueryService.getLikeStatus(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.LIKE_STATUS_READ_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.LIKE_STATUS_READ_SUCCESS, response));
    }

    @Operation(
            summary = "좋아요 등록",
            description = "좋아요가 성공적으로 등록되었는지 확인합니다."
    )
    @PostMapping("/{articleId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> likeArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        LikeResponseDTO response = articleLikeService.likeArticle(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.LIKE_CREATE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.LIKE_CREATE_SUCCESS, response));
    }

    @Operation(
            summary = "좋아요 취소",
            description = "좋아요가 성공적으로 취소되었는지 확인합니다."
    )
    @DeleteMapping("/{articleId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> unlikeArticle(
            @PathVariable Long articleId,
            HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);
        Long memberId = extractMemberIdFromToken(token);
        LikeResponseDTO response = articleLikeService.unlikeArticle(articleId, memberId);
        return ResponseEntity.status(SuccessStatus.LIKE_DELETE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.LIKE_DELETE_SUCCESS, response));
    }


    private Long extractMemberIdFromToken(String token) {
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
        if (token == null) {
            throw new MemberHandler(ErrorStatus.INVALID_JWT_ACCESS_TOKEN);
        }
        String socialId = jwtTokenProvider.getSubjectFromToken(token);
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND))
                .getId();
    }
}
