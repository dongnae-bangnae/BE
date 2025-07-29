package DNBN.spring.converter;

import DNBN.spring.domain.Curation;
import DNBN.spring.web.dto.response.CurationResponseDTO;

public class CurationConverter {
    public static CurationResponseDTO toCurationResponseDTO(Curation curation) {
        return CurationResponseDTO.builder()
                .curationId(curation.getCurationId())
                .memberId(curation.getMember().getId())
                .regionId(curation.getRegion().getId())
                .title(curation.getTitle())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .likePlaces(
                        curation.getCurationPlaces().stream()
                                .map(cp -> CurationResponseDTO.Places.builder()
                                        .likePlaceId(cp.getPlace().getPlaceId())
                                        .name(cp.getPlace().getTitle())
                                        .pinCategory(cp.getPlace().getPinCategory().name())
                                        .build()
                                ).toList()
                )
                .build();
    }

    public static CurationResponseDTO toCurationPreviewDTO(Curation curation) {
        return CurationResponseDTO.builder()
                .curationId(curation.getCurationId())
                .memberId(curation.getMember().getId())
                .regionId(curation.getRegion().getId())
                .title(curation.getTitle())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .build();
    }
}
