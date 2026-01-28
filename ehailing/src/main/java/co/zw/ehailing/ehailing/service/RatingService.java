package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.SubmitRatingRequest;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Rating;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.RatingRepository;
import co.zw.ehailing.ehailing.repository.RideRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public Rating submitRating(SubmitRatingRequest request, String reviewerPhone) {
        User reviewer = userRepository.findByPhone(reviewerPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Reviewer not found"));

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Determine reviewee
        User reviewee;
        if (ride.getRideRequest().getCustomer().getId().equals(reviewer.getId())) {
            // Reviewer is Customer -> Reviewee is Driver
            reviewee = ride.getDriver().getUser();
        } else if (ride.getDriver().getUser().getId().equals(reviewer.getId())) {
            // Reviewer is Driver -> Reviewee is Customer
            reviewee = ride.getRideRequest().getCustomer();
        } else {
            throw new RuntimeException("User is not a participant in this ride");
        }
        
        // Check if already rated (Optional: logic to prevent double rating)

        Rating rating = Rating.builder()
                .ride(ride)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .score(request.getScore())
                .comment(request.getComment())
                .build();

        Rating savedRating = ratingRepository.save(rating);

        // Update Driver's aggregate rating if reviewee is a driver
        driverRepository.findByUserId(reviewee.getId()).ifPresent(driver -> {
            Double newAvg = ratingRepository.findAverageRatingByUserId(reviewee.getId());
            driver.setRating(newAvg);
            driverRepository.save(driver);
        });

        return savedRating;
    }

    public Double getUserAverageRating(Long userId) {
        return ratingRepository.findAverageRatingByUserId(userId);
    }
}
