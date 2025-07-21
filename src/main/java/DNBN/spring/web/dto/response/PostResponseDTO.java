package DNBN.spring.web.dto.response;

// 후에 article과 합칠 예정

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewListDTO {
        List<PostPreViewDTO> postList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewDTO {
        Long articleId;
        Long memberId;
        Long categoryId;
        Long placeId;
        Long regionId;
        String username;
        String title;
        String content;
        Long likeCount;
        Long spamCount;
        LocalDateTime createdAt;
    }
}
