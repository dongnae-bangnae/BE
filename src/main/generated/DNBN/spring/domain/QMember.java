package DNBN.spring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1966817893L;

    public static final QMember member = new QMember("member1");

    public final DNBN.spring.domain.common.QBaseEntity _super = new DNBN.spring.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isOnboardingCompleted = createBoolean("isOnboardingCompleted");

    public final ListPath<DNBN.spring.domain.mapping.LikePlace, DNBN.spring.domain.mapping.QLikePlace> likePlaceList = this.<DNBN.spring.domain.mapping.LikePlace, DNBN.spring.domain.mapping.QLikePlace>createList("likePlaceList", DNBN.spring.domain.mapping.LikePlace.class, DNBN.spring.domain.mapping.QLikePlace.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<DNBN.spring.domain.enums.Provider> provider = createEnum("provider", DNBN.spring.domain.enums.Provider.class);

    public final StringPath socialId = createString("socialId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

