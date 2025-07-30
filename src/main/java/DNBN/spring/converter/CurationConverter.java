package DNBN.spring.converter;

import DNBN.spring.domain.Curation;
import DNBN.spring.web.dto.response.CurationResponseDTO;

public class CurationConverter {
    public static CurationResponseDTO.CurationDetailDTO toCurationResponseDTO(Curation curation) {
        return CurationResponseDTO.CurationDetailDTO.builder()
                .curationId(curation.getCurationId())
                .regionId(curation.getRegion().getId())
                .regionName(curation.getRegion().getFullName())
                .title(curation.getTitle())
                .thumbnailImageUrl(curation.getThumbnailImageUrl())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .likePlaces(
                        curation.getCurationPlaces().stream()
                                .map(cp -> CurationResponseDTO.CurationDetailDTO.Places.builder()
                                        .likePlaceId(cp.getPlace().getPlaceId())
                                        .name(cp.getPlace().getTitle())
                                        .pinCategory(cp.getPlace().getPinCategory().name())
                                        .build()
                                ).toList()
                )
                .build();
    }

    public static CurationResponseDTO.CurationPreviewDTO toCurationPreviewDTO(Curation curation) {
        return CurationResponseDTO.CurationPreviewDTO.builder()
                .curationId(curation.getCurationId())
                .regionId(curation.getRegion().getId())
                .regionName(curation.getRegion().getFullName())
                .title(curation.getTitle())
                .thumbnailImageUrl(curation.getThumbnailImageUrl())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .build();
    }

    public static CurationResponseDTO.CurationDetailDTO toCurationDetailDTO(Curation curation) {
        return CurationResponseDTO.CurationDetailDTO.builder()
                .curationId(curation.getCurationId())
                .regionId(curation.getRegion().getId())
                .regionName(curation.getRegion().getFullName())
                .title(curation.getTitle())
                .thumbnailImageUrl(curation.getThumbnailImageUrl())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .likePlaces(
                        curation.getCurationPlaces().stream()
                                .map(cp -> CurationResponseDTO.CurationDetailDTO.Places.builder()
                                        .likePlaceId(cp.getPlace().getPlaceId())
                                        .name(cp.getPlace().getTitle())
                                        .pinCategory(cp.getPlace().getPinCategory().name())
                                        .build()
                                ).toList()
                )
                .build();
    }
}
