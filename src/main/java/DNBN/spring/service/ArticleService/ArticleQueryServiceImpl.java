package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Region;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryServiceImpl implements ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final LikeRegionRepository likeRegionRepository;

    @Override
    public Page<Article> getArticleListByRegion(Long memberId, Integer page) {
        List<Long> regionIds = likeRegionRepository.findRegionIdsByMemberId(memberId);

        if (regionIds.isEmpty()) {
            throw new IllegalArgumentException("관심 지역이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return articleRepository.findAllByRegion_IdIn(regionIds, pageable);
    }
}

