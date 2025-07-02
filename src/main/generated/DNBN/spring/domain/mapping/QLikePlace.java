package DNBN.spring.domain.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikePlace is a Querydsl query type for LikePlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikePlace extends EntityPathBase<LikePlace> {

    private static final long serialVersionUID = 1851790575L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikePlace likePlace = new QLikePlace("likePlace");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DNBN.spring.domain.QMember member;

    public final DNBN.spring.domain.QRegion region;

    public QLikePlace(String variable) {
        this(LikePlace.class, forVariable(variable), INITS);
    }

    public QLikePlace(Path<? extends LikePlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikePlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikePlace(PathMetadata metadata, PathInits inits) {
        this(LikePlace.class, metadata, inits);
    }

    public QLikePlace(Class<? extends LikePlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new DNBN.spring.domain.QMember(forProperty("member")) : null;
        this.region = inits.isInitialized("region") ? new DNBN.spring.domain.QRegion(forProperty("region")) : null;
    }

}

