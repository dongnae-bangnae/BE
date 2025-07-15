package DNBN.spring.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpamStatusResponseDTO {
    private boolean spamed;
}