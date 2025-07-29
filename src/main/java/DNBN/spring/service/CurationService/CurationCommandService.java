package DNBN.spring.service.CurationService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.response.CurationResponseDTO;

import java.time.LocalDate;

public interface CurationCommandService {
    CurationResponseDTO generateCuration();
    LocalDate getStartOfThisWeek();
}
