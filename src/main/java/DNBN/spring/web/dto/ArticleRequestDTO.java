package DNBN.spring.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 핀이 이미 등록되어 있는 경우의 게시글 생성을 위한 요청 DTO
 */
public record ArticleRequestDTO(
    @NotNull(message = "카테고리 ID는 필수입니다.") Long categoryId,
    @NotNull(message = "장소 ID는 필수입니다.") Long placeId,
    @NotNull(message = "지역 ID는 필수입니다.") Long regionId,
    @NotBlank(message = "제목은 필수입니다.") String title,
    @NotNull(message = "날짜는 필수입니다.") LocalDate date,
    @NotBlank(message = "내용은 필수입니다.") String content
) {}
