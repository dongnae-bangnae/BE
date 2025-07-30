package DNBN.spring.repository.PlaceRepository;

import DNBN.spring.domain.Place;
import DNBN.spring.domain.QPlace;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QPlace place = QPlace.place;

    @Override
    public List<Place> findAllInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        BooleanBuilder b = new BooleanBuilder();
        b.and(place.latitude.between(latMin, latMax))
                .and(place.longitude.between(lngMin, lngMax));
//                .and(place.deletedAt.isNull()); // 필요하다면 삭제 여부 체크

        return queryFactory
                .selectFrom(place)
                .where(b)
                .orderBy(place.createdAt.desc())
                .fetch();
    }
}
