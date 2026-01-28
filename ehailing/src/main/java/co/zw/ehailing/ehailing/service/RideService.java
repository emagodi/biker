package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.AcceptBidRequest;
import co.zw.ehailing.ehailing.enums.RideRequestStatus;
import co.zw.ehailing.ehailing.enums.RideStatus;
import co.zw.ehailing.ehailing.model.Bid;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.model.RideRequest;
import co.zw.ehailing.ehailing.repository.BidRepository;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.RideRepository;
import co.zw.ehailing.ehailing.repository.RideRequestRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final RideRequestRepository rideRequestRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final WalletService walletService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Ride acceptBid(AcceptBidRequest request, String customerPhone) {
        var customer = userRepository.findByPhone(customerPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        var bid = bidRepository.findById(request.getBidId())
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        var rideRequest = bid.getRideRequest();

        // Security Check: Ensure the user accepting is the one who made the request
        if (!rideRequest.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized to accept this bid");
        }

        if (rideRequest.getStatus() != RideRequestStatus.BIDDING) {
            throw new RuntimeException("Ride request is not in bidding state");
        }

        // Create Ride
        Ride ride = Ride.builder()
                .rideRequest(rideRequest)
                .driver(bid.getDriver())
                .finalPrice(bid.getPrice())
                .status(RideStatus.STARTED) // Or create a SCHEDULED status, but STARTED for simplicity if instant
                .build();
        
        Ride savedRide = rideRepository.save(ride);

        // Update Request Status
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequestRepository.save(rideRequest);

        // Notify Driver
        messagingTemplate.convertAndSendToUser(
                bid.getDriver().getUser().getPhone(),
                "/queue/ride-accepted",
                savedRide
        );

        // Notify other bidders (optional - simply closing the request loop)
        List<Bid> otherBids = bidRepository.findByRideRequestId(rideRequest.getId());
        for (Bid otherBid : otherBids) {
            if (!otherBid.getId().equals(bid.getId())) {
                 // Could send a "Bid Rejected" or "Ride Taken" message
                 messagingTemplate.convertAndSendToUser(
                    otherBid.getDriver().getUser().getPhone(),
                    "/queue/ride-missed",
                    "Ride " + rideRequest.getId() + " was taken by another driver."
                 );
            }
        }

        return savedRide;
    }

    @Transactional
    public Ride startRide(Long rideId, String driverPhone) {
        var ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        
        validateDriver(ride, driverPhone);

        if (ride.getStatus() != RideStatus.STARTED) { 
            // If we had a SCHEDULED status, we would switch to STARTED here. 
            // Since we initialized as STARTED, this might be redundant or for re-confirming pickup.
            // Let's assume we want to track "Arrived at Pickup" vs "Trip Started".
            // For MVP, let's keep it simple.
        }

        // Notify Customer
        messagingTemplate.convertAndSendToUser(
                ride.getRideRequest().getCustomer().getPhone(),
                "/queue/ride-status",
                "Ride Started"
        );
        
        return ride;
    }

    @Transactional
    public Ride completeRide(Long rideId, String driverPhone) {
        var ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        validateDriver(ride, driverPhone);

        if (ride.getStatus() == RideStatus.COMPLETED) {
             throw new RuntimeException("Ride already completed");
        }

        // Update Status
        ride.setStatus(RideStatus.COMPLETED);
        Ride savedRide = rideRepository.save(ride);

        // Deduct Commission
        walletService.deductCommission(ride.getDriver().getId(), ride.getFinalPrice());

        // Notify Customer
        messagingTemplate.convertAndSendToUser(
                ride.getRideRequest().getCustomer().getPhone(),
                "/queue/ride-status",
                "Ride Completed. Fee: " + ride.getFinalPrice()
        );

        return savedRide;
    }

    private void validateDriver(Ride ride, String driverPhone) {
        if (!ride.getDriver().getUser().getPhone().equals(driverPhone)) {
            throw new RuntimeException("Unauthorized: Not the driver for this ride");
        }
    }
}
