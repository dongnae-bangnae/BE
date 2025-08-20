package DNBN.spring.service.CategoryService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.enums.Color;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.request.CategoryRequestDTO;
import DNBN.spring.web.dto.response.CategoryResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        // soft delete 된 row 무시하고 중복 체크
        boolean exists = categoryRepository.existsByNameAndMemberAndDeletedAtIsNull(dto.name(), member);
        if (exists) {
            throw new CategoryHandler(ErrorStatus.CATEGORY_DUPLICATE_NAME);
        }

        Category category = Category.builder()
                .name(dto.name())
                .color(Color.from(dto.color()))
                .member(member)
                .build();

        categoryRepository.save(category);
        return new CategoryResponseDTO(category.getCategoryId(), category.getName(), category.getColor().name());
    }
//        try {
//            Category category = Category.builder()
//                    .name(dto.name())
//                    .color(Color.from(dto.color()))
//                    .member(member)
//                    .build();
//
//            categoryRepository.save(category);
//            return new CategoryResponseDTO(category.getCategoryId(), category.getName(), category.getColor().name());
//        } catch (DataIntegrityViolationException e) {
//            throw new CategoryHandler(ErrorStatus.CATEGORY_DUPLICATE_NAME);
//        }



    public CategoryResponseDTO update(Long memberId, Long categoryId, CategoryRequestDTO dto) {
        Category category = getOwnedCategory(memberId, categoryId);
        try {
            // 변경사항이 있을 때만 업데이트 시도
            if (!category.getName().equals(dto.name()) || !category.getColor().equals(Color.from(dto.color()))) {
                category.update(dto.name(), Color.from(dto.color()));
                categoryRepository.save(category); // 변경사항을 DB에 반영
            }

            return new CategoryResponseDTO(category.getCategoryId(), category.getName(), category.getColor().name());
        } catch (DataIntegrityViolationException e) {
            throw new CategoryHandler(ErrorStatus.CATEGORY_DUPLICATE_NAME);
        }
    }

    public void delete(Long memberId, Long categoryId) {
        Category category = getOwnedCategory(memberId, categoryId);
        category.softDelete();
        categoryRepository.save(category);
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
