package DNBN.spring.repository.ProfileImageRepository;

import DNBN.spring.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByMemberId(Long reviewId);
}
