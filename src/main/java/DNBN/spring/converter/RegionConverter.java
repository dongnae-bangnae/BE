package DNBN.spring.converter;

import DNBN.spring.domain.Region;
import DNBN.spring.web.dto.RegionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    public RegionResponseDTO.RegionFullNameDTO toRegionPreviewDTO(Region region) {
        return RegionResponseDTO.RegionFullNameDTO.builder()
                .regionId(region.getId())
                .province(region.getProvince())
                .city(region.getCity())
                .district(region.getDistrict())
                .build();
    }
}
