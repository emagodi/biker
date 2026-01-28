package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.dto.CreateRideRequest;
import co.zw.ehailing.ehailing.dto.PlaceBidRequest;
import co.zw.ehailing.ehailing.model.Bid;
import co.zw.ehailing.ehailing.model.RideRequest;
import co.zw.ehailing.ehailing.service.BidService;
import co.zw.ehailing.ehailing.dto.AcceptBidRequest;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.service.RideService;
import co.zw.ehailing.ehailing.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideRequestService rideRequestService;
    private final BidService bidService;
    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideRequest> requestRide(
            @RequestBody CreateRideRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideRequestService.createRideRequest(request, userDetails.getUsername()));
    }

    @PostMapping("/bid")
    public ResponseEntity<Bid> placeBid(
            @RequestBody PlaceBidRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(bidService.placeBid(request, userDetails.getUsername()));
    }

    @PostMapping("/accept-bid")
    public ResponseEntity<Ride> acceptBid(
            @RequestBody AcceptBidRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideService.acceptBid(request, userDetails.getUsername()));
    }

    @GetMapping("/open")
    public ResponseEntity<List<RideRequest>> getOpenRequests() {
        return ResponseEntity.ok(rideRequestService.getOpenRequests());
    }

    @GetMapping("/{rideId}/bids")
    public ResponseEntity<List<Bid>> getBids(
            @PathVariable Long rideId
    ) {
        return ResponseEntity.ok(bidService.getBidsForRequest(rideId));
    }

    @PostMapping("/{rideId}/start")
    public ResponseEntity<Ride> startRide(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideService.startRide(rideId, userDetails.getUsername()));
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<Ride> completeRide(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(rideService.completeRide(rideId, userDetails.getUsername()));
    }
}
