package DNBN.spring.repository.SavePlaceRepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import DNBN.spring.domain.mapping.QSavePlace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import DNBN.spring.domain.mapping.SavePlace;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SavePlaceRepositoryImpl implements SavePlaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QSavePlace savePlace = QSavePlace.savePlace;

    @Override
    public List<SavePlace> findSavedPlaces(Long categoryId, Long cursor, Long limit) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(savePlace.category.categoryId.eq(categoryId));

        if (cursor != null) {
            builder.and(savePlace.id.gt(cursor));
        }

        return jpaQueryFactory.selectFrom(savePlace)
                .where(builder)
                .orderBy(savePlace.id.asc())
                .limit(limit)
                .fetch();
    }
}
