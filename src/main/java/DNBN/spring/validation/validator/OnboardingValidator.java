package DNBN.spring.validation.validator;

import DNBN.spring.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class OnboardingValidator {

    public boolean isCompleteOnboarding(Member member) {
        boolean hasNickname = member.getNickname() != null && !member.getNickname().isBlank();
        boolean hasRegions = member.getLikeRegionList() != null && !member.getLikeRegionList().isEmpty();

        return hasNickname && hasRegions;
    }
}
