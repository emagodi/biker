package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.service.RideHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class RideHistoryController {

    private final RideHistoryService rideHistoryService;

    @GetMapping
    public ResponseEntity<List<Ride>> getRideHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideHistoryService.getUserRideHistory(userDetails.getUsername()));
    }
}
