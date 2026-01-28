package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRevieweeId(Long revieweeId);
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.reviewee.id = :userId")
    Double findAverageRatingByUserId(Long userId);
}
