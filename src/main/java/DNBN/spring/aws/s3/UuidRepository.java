package DNBN.spring.aws.s3;


import DNBN.spring.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
