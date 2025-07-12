package DNBN.spring.repository.CategoryRepository;

import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByMember(Member member);
    List<Category> findAllByPlace(Place place);
    List<Category> findAllByRegion(Region region);
}

