package DNBN.spring.repository.ArticleRepository;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByMember(Member member);
    List<Article> findAllByCategory(Category category);
    List<Article> findAllByPlace(Place place);
    List<Article> findAllByRegion(Region region);

    Page<Article> findAllByRegion_IdIn(List<Long> regionIds, Pageable pageable);

    Page<Article> findAllByPlace_IdIn(List<Long> placeIds, Pageable pageable);

    Optional<Article> findTopByHashtagOrderByLikesCountDescCreatedAtAsc(String keyword);

    List<Article> findByPlaceOrderByCreatedAtAsc(Place place);
}

