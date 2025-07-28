package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import DNBN.spring.domain.QRegion;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QRegion region = QRegion.region;

    @Override
    public List<Region> searchByKeyword(String keyword, Long cursor, int limit) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    region.province.containsIgnoreCase(keyword)
                            .or(region.city.containsIgnoreCase(keyword))
                            .or(region.district.containsIgnoreCase(keyword))
            );
        }

        if (cursor != null) {
            builder.and(region.id.gt(cursor));
        }

        return jpaQueryFactory.selectFrom(region)
                .where(builder)
                .orderBy(region.id.asc())
                .limit(limit)
                .fetch();
    }
}
