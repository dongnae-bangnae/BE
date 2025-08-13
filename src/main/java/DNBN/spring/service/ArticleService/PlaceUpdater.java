package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PlaceUpdater {
    public void updatePlaceEntity(Place place, ArticleUpdateRequestDTO request) {
        if (request.placeName() != null) {
            place.updateTitle(request.placeName());
        }
        if (request.pinCategory() != null) {
            try {
                PinCategory newPinCategory = PinCategory.valueOf(request.pinCategory().toUpperCase());
                place.updatePinCategory(newPinCategory);
            } catch (IllegalArgumentException e) {
                throw new PlaceHandler(ErrorStatus.PIN_CATEGORY_INVALID);
            }
        }
    }
}

