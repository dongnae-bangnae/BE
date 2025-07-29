package DNBN.spring.service.CurationService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.response.CurationResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface CurationCommandService {
    List<CurationResponseDTO> generateCurations();
    LocalDate getStartOfThisWeek();
}
