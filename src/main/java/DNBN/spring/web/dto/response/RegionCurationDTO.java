package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionCurationDTO {
    private Long regionId;
    private String regionName;
    private List<CurationResponseDTO> curations;
}
