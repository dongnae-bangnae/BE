package DNBN.spring.apiPayload.code.status;

import DNBN.spring.apiPayload.code.BaseCode;
import DNBN.spring.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON2000", "성공입니다."),

    // 멤버 관련 응답
    MEMBER_NEEDS_ONBOARDING(HttpStatus.CREATED, "MEMBER2001", "신규 유저입니다. 온보딩이 필요합니다."),
    MEMBER_ALREADY_LOGIN(HttpStatus.OK, "MEMBER2002", "이미 등록된 유저입니다."),
    MEMBER_ONBOARDING_SUCCESS(HttpStatus.CREATED, "MEMBER2003", "온보딩 정보를 저장했습니다."),
    MEMBER_INFO_RETRIEVED(HttpStatus.OK, "MEMBER2004", "유저 정보를 성공적으로 조회했습니다."),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER2005", "로그아웃이 완료되었습니다."),
    MEMBER_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "MEMBER2006", "회원 탈퇴가 완료되었습니다."),
    MEMBER_PROFILE_IMAGE_UPDATED(HttpStatus.OK, "MEMBER2007", "프로필 이미지가 성공적으로 변경되었습니다."),
    MEMBER_NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "MEMBER2008", "닉네임이 성공적으로 변경되었습니다."),
    MEMBER_REGION_UPDATE_SUCCESS(HttpStatus.OK, "MEMBER2009", "관심 동네가 성공적으로 변경되었습니다."),
    MEMBER_PROFILE_IMAGE_UPDATE_SUCCESS(HttpStatus.OK, "MEMBER2010", "프로필 이미지가 성공적으로 변경되었습니다."),

    // 장소 저장 관련 응답
    SAVED_PLACE_CREATE_SUCCESS(HttpStatus.CREATED, "SAVE_PLACE2001", "장소가 카테고리에 성공적으로 저장되었습니다."),
    PLACE_MAP_LIST_READ_SUCCESS(HttpStatus.OK, "PLACE2001", "지도 내 장소 목록을 성공적으로 조회했습니다."),

    // 댓글 관련 응답
    COMMENT_CREATE_SUCCESS(HttpStatus.CREATED, "COMMENT_CREATE_SUCCESS", "댓글이 정상적으로 작성되었습니다."),
    COMMENT_UPDATE_SUCCESS(HttpStatus.OK, "COMMENT_UPDATE_SUCCESS", "댓글이 정상적으로 수정되었습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "COMMENT_DELETE_SUCCESS", "댓글이 정상적으로 삭제되었습니다."),
    COMMENT_READ_SUCCESS(HttpStatus.OK, "COMMENT_READ_SUCCESS", "댓글을 정상적으로 조회했습니다."),
    COMMENT_REPLY_READ_SUCCESS(HttpStatus.OK, "COMMENT_REPLY_READ_SUCCESS", "대댓글을 정상적으로 조회했습니다."),
    COMMENT_LIST_READ_SUCCESS(HttpStatus.OK, "COMMENT_LIST_READ_SUCCESS", "댓글 목록을 정상적으로 조회했습니다."),

    // 동네 검색 관련 응답
    REGION_SEARCH_SUCCESS(HttpStatus.OK, "REGION2001", "지역 검색 결과입니다."),

    // 게시물 관련 응답
    ARTICLE_CREATE_SUCCESS(HttpStatus.CREATED, "ARTICLE_CREATE_SUCCESS", "게시물이 정상적으로 작성되었습니다."),
    ARTICLE_READ_SUCCESS(HttpStatus.OK, "ARTICLE_READ_SUCCESS", "게시물 상세페이지가 정상적으로 조회되었습니다."),
    ARTICLE_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "ARTICLE_DELETE_SUCCESS", "게시물이 정상적으로 삭제되었습니다."),
    ARTICLE_UPDATE_SUCCESS(HttpStatus.OK, "ARTICLE_UPDATE_SUCCESS", "게시물이 정상적으로 수정되었습니다."),

    // 좋아요 관련 응답
    LIKE_STATUS_READ_SUCCESS(HttpStatus.OK, "LIKE2001", "좋아요 상태를 성공적으로 조회했습니다."),
    LIKE_CREATE_SUCCESS(HttpStatus.CREATED, "LIKE2002", "좋아요가 성공적으로 등록되었습니다."),
    LIKE_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "LIKE2003", "좋아요가 성공적으로 취소되었습니다."),

    // 스팸(광고 의심 신고) 관련 응답
    SPAM_STATUS_READ_SUCCESS(HttpStatus.OK, "SPAM2001", "광고 의심 신고 상태를 성공적으로 조회했습니다."),
    SPAM_CREATE_SUCCESS(HttpStatus.CREATED, "SPAM2002", "광고 의심 신고가 성공적으로 등록되었습니다."),
    SPAM_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "SPAM2003", "광고 의심 신고가 성공적으로 취소되었습니다."),

    // 카테고리 관련 응답
    CATEGORY_LIST_READ_SUCCESS(HttpStatus.OK, "CATEGORY2001", "카테고리 목록을 성공적으로 조회했습니다."),
    CATEGORY_CREATE_SUCCESS(HttpStatus.CREATED, "CATEGORY2002", "카테고리가 성공적으로 등록되었습니다."),
    CATEGORY_UPDATE_SUCCESS(HttpStatus.OK, "CATEGORY2003", "카테고리가 성공적으로 수정되었습니다."),
    CATEGORY_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "CATEGORY2004", "카테고리가 성공적으로 삭제되었습니다."),
    CATEGORY_PLACE_LIST_READ_SUCCESS(HttpStatus.OK, "CATEGORY2005", "카테고리에 저장된 장소 목록을 성공적으로 조회했습니다."),
    CATEGORY_ARTICLE_LIST_READ_SUCCESS(HttpStatus.OK, "CATEGORY2006", "카테고리에 작성된 게시물 목록을 성공적으로 조회했습니다."),

    // 큐레이션 관련 응답
    CURATION_LIST_READ_SUCCESS(HttpStatus.OK, "CURATION2001", "큐레이션 리스트를 성공적으로 조회했습니다."),
    CURATION_DETAIL_READ_SUCCESS(HttpStatus.OK, "CURATION2002", "큐레이션 상세 정보를 성공적으로 조회했습니다."),
    CURATION_GENERATE_SUCCESS(HttpStatus.CREATED, "CURATION2003", "큐레이션이 성공적으로 생성되었습니다."),

    // 기본 이미지 관련 응답
    DEFAULT_IMAGE_LIST_READ_SUCCESS(HttpStatus.OK, "DEFAULT_IMAGE2001", "기본 이미지 목록을 성공적으로 조회했습니다."),

    // 홈 화면 관련 응답
    HOME_ARTICLE_LIST_READ_SUCCESS(HttpStatus.OK, "HOME2001", "홈 화면 새 글 리스트를 성공적으로 조회했습니다."),
    HOME_CHALLENGE_DETAIL_READ_SUCCESS(HttpStatus.OK, "HOME2002", "챌린지 상세 정보를 성공적으로 조회했습니다."),
    HOME_TOP_ARTICLE_READ_SUCCESS(HttpStatus.OK, "HOME2003", "챌린지 좋아요 1등 게시물을 성공적으로 조회했습니다."),

    // 핑 관련 응답
    PING_SUCCESS(HttpStatus.OK, "PING2001", "서버 핑 응답 성공입니다."),

    // 알림 관련 응답
    NOTIFICATION_COMMENT_LIST_SUCCESS(HttpStatus.OK, "NOTIFICATION2001", "댓글 알림 목록을 성공적으로 조회했습니다."),
    NOTIFICATION_COMMENT_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "NOTIFICATION2002", "댓글 알림을 성공적으로 삭제했습니다."),
    NOTIFICATION_SPAM_LIST_SUCCESS(HttpStatus.OK, "NOTIFICATION2003", "광고 의심 알림 목록을 성공적으로 조회했습니다."),
    NOTIFICATION_SPAM_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "NOTIFICATION2004", "광고 의심 알림을 성공적으로 삭제했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
