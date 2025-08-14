package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.CommentService.CommentCommandService;
import DNBN.spring.service.CommentService.CommentQueryService;
import DNBN.spring.web.dto.CommentCursorResponseDTO;
import DNBN.spring.web.dto.CommentRequestDTO;
import DNBN.spring.web.dto.CommentResponseDTO;
import DNBN.spring.web.dto.CommentUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/{articleId}/comments")
@SecurityRequirement(name = "JWT TOKEN")
public class CommentController {
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    @Operation(
        summary = "댓글 생성",
        description = "게시물에 댓글을 작성합니다. JWT 인증 필요.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<CommentResponseDTO> createComment(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long articleId,
            @RequestBody @Valid CommentRequestDTO request
    ) {
        Long memberId = memberDetails.getMember().getId();
        CommentResponseDTO response = commentCommandService.createComment(memberId, articleId, request);
        return ApiResponse.of(SuccessStatus.COMMENT_CREATE_SUCCESS, response);
    }

    @DeleteMapping("/{commentId}")
    @Operation(
        summary = "댓글 삭제",
        description = "댓글을 삭제합니다. JWT 인증 필요.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long articleId,
            @PathVariable Long commentId
    ) {
        Long memberId = memberDetails.getMember().getId();
        commentCommandService.deleteComment(memberId, commentId, articleId);
        return ApiResponse.of(SuccessStatus.COMMENT_DELETE_SUCCESS, null);
    }

    @PatchMapping("/{commentId}")
    @Operation(
        summary = "댓글 수정",
        description = "댓글 내용을 수정합니다. JWT 인증 필요.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<CommentResponseDTO> updateComment(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDTO request
    ) {
        Long memberId = memberDetails.getMember().getId();
        CommentResponseDTO response = commentCommandService.updateComment(memberId, commentId, articleId, request);
        return ApiResponse.of(SuccessStatus.COMMENT_UPDATE_SUCCESS, response);
    }

    @GetMapping
    @Operation(
        summary = "부모 댓글 목록 조회",
        description = "게시물의 댓글을 커서 페이징으로 조회합니다.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<CommentCursorResponseDTO> getComments(
            @PathVariable Long articleId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        CommentCursorResponseDTO response = commentQueryService.getComments(articleId, cursor, size);
        return ApiResponse.of(SuccessStatus.COMMENT_READ_SUCCESS, response);
    }

    @GetMapping("/{parentCommentId}/replies")
    @Operation(
        summary = "대댓글 목록 조회",
        description = "특정 댓글의 대댓글을 커서 페이징으로 조회합니다.",
        security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<CommentCursorResponseDTO> getReplies(
            @PathVariable Long parentCommentId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        CommentCursorResponseDTO response = commentQueryService.getReplies(parentCommentId, cursor, size);
        return ApiResponse.of(SuccessStatus.COMMENT_REPLY_READ_SUCCESS, response);
    }
}
