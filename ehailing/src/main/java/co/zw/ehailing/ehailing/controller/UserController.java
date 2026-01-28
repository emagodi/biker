package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.dto.SubmitRatingRequest;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.service.RatingService;
import co.zw.ehailing.ehailing.service.RideHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final RatingService ratingService;
    private final RideHistoryService rideHistoryService;

    @PostMapping("/rate")
    public ResponseEntity<String> rateRide(
            @RequestBody SubmitRatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ratingService.submitRating(request, userDetails.getUsername());
        return ResponseEntity.ok("Rating submitted successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<Ride>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideHistoryService.getUserRideHistory(userDetails.getUsername()));
    }
}
