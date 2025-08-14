package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.NotificationService.NotificationCommandService;
import DNBN.spring.service.NotificationService.NotificationQueryService;
import DNBN.spring.web.dto.response.NotificationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "JWT TOKEN")
public class NotificationRestController {

    private final NotificationQueryService querySvc;
    private final NotificationCommandService cmdSvc;

    @Operation(summary = "댓글 알림 목록 조회", description = "댓글 알림 목록을 커서 기반 페이징으로 조회합니다.")
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<NotificationResponseDTO.commentListDTO>> listComments(
            @AuthenticationPrincipal MemberDetails user,
            @Parameter(
                    name        = "cursor",
                    description = "다음 페이지 요청 시 기준이 되는 notificationId (default: null) -> hasNext = true 이면, 응답받은 cursor 값 입력 필요",
                    schema      = @Schema(type = "integer", format = "int64", defaultValue = "null")
            )
            @RequestParam(required = false) Long cursor,
            @Parameter(name = "limit", description = "최대 응답 개수 (default: 20)", example = "20")
            @RequestParam(defaultValue = "20") Long limit) {
        var result = querySvc.getCommentNotifications(user.getMember().getId(), cursor, limit);
        return ResponseEntity.status(SuccessStatus.NOTIFICATION_COMMENT_LIST_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.NOTIFICATION_COMMENT_LIST_SUCCESS, result));
    }

    @Operation(summary = "댓글 알림 삭제", description = "사용자가 받은 특정 댓글 알림을 삭제합니다.")
    @DeleteMapping("/comments/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> hideComment(
            @AuthenticationPrincipal MemberDetails user,
            @PathVariable Long notificationId) {
        cmdSvc.hideNotification(user.getMember().getId(), notificationId);
        return ResponseEntity.status(SuccessStatus.NOTIFICATION_COMMENT_DELETE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.NOTIFICATION_COMMENT_DELETE_SUCCESS, null));
    }

    @Operation(summary="광고 의심 알림 목록 조회", description="광고 의심 횟수(10회,20회) 단위로 생성된 알림을 커서 기반 조회합니다.")
    @GetMapping("/spams")
    public ResponseEntity<ApiResponse<NotificationResponseDTO.SpamListDTO>> listSpams(
            @AuthenticationPrincipal MemberDetails user,
            @Parameter(
                    name        = "cursor",
                    description = "다음 페이지 요청 시 기준이 되는 notificationId (default: null) -> hasNext = true 이면, 응답받은 cursor 값 입력 필요",
                    schema      = @Schema(type = "integer", format = "int64", defaultValue = "null")
            )
            @RequestParam(required=false) Long cursor,
            @Parameter(name = "limit", description = "최대 응답 개수 (default: 20)", example = "20")
            @RequestParam(defaultValue="20") Long limit
    ) {
        var result = querySvc.getSpamNotifications(user.getMember().getId(), cursor, limit);
        return ResponseEntity.status(SuccessStatus.NOTIFICATION_SPAM_LIST_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.NOTIFICATION_SPAM_LIST_SUCCESS, result));
    }

    @Operation(summary="광고 의심 알림 삭제", description="사용자가 받은 특정 광고 의심 알림을 삭제합니다.")
    @DeleteMapping("/spams/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> hideSpam(
            @AuthenticationPrincipal MemberDetails user,
            @PathVariable Long notificationId
    ) {
        cmdSvc.hideNotification(user.getMember().getId(), notificationId);
        return ResponseEntity.status(SuccessStatus.NOTIFICATION_SPAM_DELETE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.NOTIFICATION_SPAM_DELETE_SUCCESS, null));
    }
}
