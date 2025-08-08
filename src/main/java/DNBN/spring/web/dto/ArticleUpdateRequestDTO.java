package DNBN.spring.web.dto;

import java.time.LocalDate;

/**
 * 게시글 수정 요청 DTO. 모든 필드는 null이 될 수 있습니다.
 */
public record ArticleUpdateRequestDTO(
    Long categoryId,
    Long regionId,
    Long placeId,
    String placeName,
    String pinCategory,
    String title,
    LocalDate date,
    String content
) {}

