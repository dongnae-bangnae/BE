package DNBN.spring.service.CategoryService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.CategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public List<CategoryResponseDTO> getMyCategories(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return categoryRepository.findAllByMemberAndDeletedAtIsNull(member).stream()
                .map(c -> new CategoryResponseDTO(c.getCategoryId(), c.getName(), c.getColor().name()))
                .toList();
    }
}
