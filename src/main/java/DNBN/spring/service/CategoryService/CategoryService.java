package DNBN.spring.service.CategoryService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.DuplicateCategoryException;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.enums.Color;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.CategoryRequestDTO;
import DNBN.spring.web.dto.CategoryResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public CategoryResponseDTO create(Long memberId, CategoryRequestDTO dto) {
        Member member = getMember(memberId);
        if (categoryRepository.existsByNameAndMemberAndDeletedAtIsNull(dto.name(), member)) {
            throw new CategoryHandler(ErrorStatus.CATEGORY_DUPLICATE_NAME);
        }

        Place place = null;
        if (dto.placeId() != null) {
            place = placeRepository.findById(dto.placeId())
                    .orElseThrow(() -> new CategoryHandler(ErrorStatus.PLACE_NOT_FOUND));
        }

        Region region = null;
        if (dto.regionId() != null) {
            region = regionRepository.findById(dto.regionId())
                    .orElseThrow(() -> new CategoryHandler(ErrorStatus.REGION_NOT_FOUND));
        }

        Category category = Category.builder()
                .name(dto.name())
                .color(Color.from(dto.color()))
                .member(member)
                .place(place)
                .region(region)
                .build();

        categoryRepository.save(category);
        return new CategoryResponseDTO(category.getCategoryId(), category.getName(), category.getColor().name());
    }


    public CategoryResponseDTO update(Long memberId, Long categoryId, CategoryRequestDTO dto) {
        Category category = getOwnedCategory(memberId, categoryId);
        category.update(dto.name(), Color.from(dto.color()));
        return new CategoryResponseDTO(category.getCategoryId(), category.getName(), category.getColor().name());
    }

    public void delete(Long memberId, Long categoryId) {
        Category category = getOwnedCategory(memberId, categoryId);
        category.softDelete();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new CategoryHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Category getOwnedCategory(Long memberId, Long categoryId) {
        Member member = getMember(memberId);
        return categoryRepository.findByCategoryIdAndMemberAndDeletedAtIsNull(categoryId, member)
                .orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
    }
}
