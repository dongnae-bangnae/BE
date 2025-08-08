package DNBN.spring.apiPayload.code.status;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적�� 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON4000","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON4001","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "금지된 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON4004", "권한이 없습니다."),

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수입니다."),
    ONBOARDING_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 사용자입니다. 온보딩을 마치지 않았습니다."),
    INVALID_REGION_COUNT(HttpStatus.BAD_REQUEST, "MEMBER4004", "좋아하는 동네는 최소 1개, 최대 3개까지 선택할 수 있습니다."),
    SOCIALID_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4005", "socialId가 없습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "IMAGE4006", "이미지 형식의 파일만 업로드할 수 있습니다."),
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "IMAGE4007", "이미지 파일은 10MB 이하로 업로드해주세요."),
    
    // 게시물
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),
    ARTICLE_TITLE_LENGTH_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "ARTICLE4002", "제목은 2~100자여야 합니다."),
    ARTICLE_CONTENT_LENGTH_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "ARTICLE4003", "내용은 10~5000자여야 합니다."),
    ARTICLE_FORBIDDEN(HttpStatus.FORBIDDEN, "ARTICLE4004", "해당 게시글에 대한 권한이 없습니다."),
    ARTICLE_ALREADY_DELETED(HttpStatus.CONFLICT, "ARTICLE4005", "이미 삭제된 게시글입니다."),
    ARTICLE_TITLE_NULL_ERROR(HttpStatus.BAD_REQUEST, "ARTICLE4006", "제목은 필수입니다."),
    ARTICLE_CONTENT_NULL_ERROR(HttpStatus.BAD_REQUEST, "ARTICLE4007", "내용은 필수입니다."),

    // articlePhoto & S3
    ARTICLE_PHOTO_MAIN_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "ARTICLEPHOTO4002", "대표 이미지는 필수입니다."),
    ARTICLE_PHOTO_IMAGE_INVALID_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "ARTICLEPHOTO4005", "지원하지 않는 이미지 파일 형식입니다."),
    ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "ARTICLEPHOTO4003", "이미지는 최대 10장까지 등록할 수 있습니다."),
    ARTICLE_PHOTO_IMAGE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "ARTICLEPHOTO4004", "이미지 파일 크기가 너무 큽니다. (최대 10MB)"),
    ARTICLE_PHOTO_S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ARTICLEPHOTO5001", "이미지 업로드에 실패했습니다. 잠시 후 다시 시도해 주세요."),

    // article & challenge
    ARTICLE_CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND,"ARTICLECHALLENGE4001", "해당하는 해시태그를 포함한 챌린지 게시물을 찾을 수 없습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION4001", "해당 지역이 존재하지 않습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4001", "가게가 없습니다."),
    PAGE_NOT_VALID(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지는 1 이상이어야 합니다."),

    //jwt 토큰
    INVALID_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 AccessToken입니다."),
    INVALID_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4002", "유효하지 않은 RefreshToken입니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4003", "유효하지 않은 SocialToken입니다."),
    INVALID_CSRF_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4004", "유효하지 않은 CSRF Token입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "PW4001", "잘못된 비밀번호입니다."),

    // 장소
    CATEGORY_ALREADY_SAVED_FOR_PLACE(HttpStatus.CONFLICT, "SAVE_PLACE4001", "이미 해당 장소가 저장되어 있습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE4001", "해당 장소를 찾을 수 없습니다."),
    COORDINATE_PRECISION_INVALID(HttpStatus.BAD_REQUEST, "PLACE4002", "소수점 5자리까지만 입력 가능합니다."),
    PIN_CATEGORY_INVALID(HttpStatus.BAD_REQUEST, "PLACE4003", "유효하지 않은 핀 카테고리입니다."),

    // 알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4001", "알림을 찾을 수 없습니다."),
    NOTIFICATION_ALREADY_HIDDEN(HttpStatus.CONFLICT, "NOTIFICATION4002", "이미 삭제된 알림입니다."),

    //like
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST404", "해당 게시물을 찾을 수 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "LIKE4001", "이미 좋아요를 누르셨습니다."),
    NOT_LIKED(HttpStatus.NOT_FOUND, "LIKE4002", "좋아요를 누르지 않았거나, 해당 게시물을 찾을 수 없습니다."),

    //spam
    ALREADY_SPAM_REPORTED(HttpStatus.CONFLICT, "SPAM4001", "이미 광고 의심 신고를 하셨습니다."),
    NOT_SPAM_REPORTED(HttpStatus.NOT_FOUND, "SPAM4002", "광고 의심 신고를 하지 않았거나, 해당 게시물을 찾을 수 없습니다."),

    //category
    CATEGORY_INVALID_NAME(HttpStatus.BAD_REQUEST, "CATEGORY_400_INVALID_NAME", "카테고리 이름이 유효하지 않습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_404_NOT_FOUND", "해당 카테고리를 찾을 수 없습니다."),
    CATEGORY_DUPLICATE_NAME(HttpStatus.CONFLICT, "CATEGORY_409_DUPLICATE_NAME", "이미 존재하는 카테고리 이름입니다."),
    CATEGORY_ASSOCIATED_ARTICLES(HttpStatus.CONFLICT, "CATEGORY_409_ASSOCIATED_ARTICLES", "해당 카테고리에 연결된 게시물이 있어 삭제할 수 없습니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4001", "댓글을 찾을 수 없습니다."),
    COMMENT_CONTENT_NULL_ERROR(HttpStatus.BAD_REQUEST, "COMMENT4005", "댓글 내용이 비어 있습니다."),
    COMMENT_CONTENT_LENGTH_INVALID(HttpStatus.BAD_REQUEST, "COMMENT4002", "댓글 내용은 1~1000자여야 합니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMENT4003", "해당 댓글에 대한 권한이 없습니다."),
    COMMENT_ALREADY_DELETED(HttpStatus.CONFLICT, "COMMENT4004", "이미 삭제된 댓글입니다."),

    // 챌린지
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE_4001", "챌린지가 존재하지 않습니다."),

    // 큐레이션
    CURATION_NO_VALID_REGION(HttpStatus.NOT_FOUND, "CURATION_4001", "지역이 존재하지 않습니다."),
    CURATION_NOT_ENOUGH_PLACES(HttpStatus.BAD_REQUEST, "CURATION_4002", "큐레이션 생성을 위한 장소가 부족합니다."),
    CURATION_NOT_FOUND(HttpStatus.NOT_FOUND,"CURATION_4003","큐레이션이 존재하지 않습니다. 먼저 생성 후 진행하세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
