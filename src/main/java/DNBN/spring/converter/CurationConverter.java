package DNBN.spring.converter;

import DNBN.spring.domain.Curation;
import DNBN.spring.web.dto.response.CurationResponseDTO;

public class CurationConverter {
    public static CurationResponseDTO toCurationResponseDTO(Curation curation) {
        return CurationResponseDTO.builder()
                .curationId(curation.getCurationId())
                .title(curation.getTitle())
                .title(curation.getTitle())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .build();
    }
}
