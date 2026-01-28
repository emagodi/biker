package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.dto.SubmitRatingRequest;
import co.zw.ehailing.ehailing.model.Rating;
import co.zw.ehailing.ehailing.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Rating> submitRating(
            @RequestBody SubmitRatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ratingService.submitRating(request, userDetails.getUsername()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Double> getUserRating(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getUserAverageRating(userId));
    }
}
