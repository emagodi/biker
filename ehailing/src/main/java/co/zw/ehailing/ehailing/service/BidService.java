package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.BidNotification;
import co.zw.ehailing.ehailing.dto.PlaceBidRequest;
import co.zw.ehailing.ehailing.enums.RideRequestStatus;
import co.zw.ehailing.ehailing.model.Bid;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Motorcycle;
import co.zw.ehailing.ehailing.model.RideRequest;
import co.zw.ehailing.ehailing.repository.BidRepository;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.MotorcycleRepository;
import co.zw.ehailing.ehailing.repository.RideRequestRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final RideRequestRepository rideRequestRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Bid placeBid(PlaceBidRequest request, String driverPhone) {
        var user = userRepository.findByPhone(driverPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Driver user not found"));
        
        var driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        var rideRequest = rideRequestRepository.findById(request.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (rideRequest.getStatus() != RideRequestStatus.BIDDING && rideRequest.getStatus() != RideRequestStatus.REQUESTED) {
            throw new RuntimeException("Ride request is not open for bidding");
        }

        Bid bid = Bid.builder()
                .rideRequest(rideRequest)
                .driver(driver)
                .price(request.getPrice())
                .build();

        Bid savedBid = bidRepository.save(bid);

        // Notify Customer via WebSocket
        notifyCustomerOfBid(savedBid, rideRequest);

        return savedBid;
    }

    public java.util.List<Bid> getBidsForRequest(Long requestId) {
        return bidRepository.findByRideRequestId(requestId);
    }

    private void notifyCustomerOfBid(Bid bid, RideRequest rideRequest) {
        var driver = bid.getDriver();
        var motorcycle = motorcycleRepository.findByDriverId(driver.getId())
                .orElse(new Motorcycle()); // Handle safely if no bike found

        BidNotification notification = BidNotification.builder()
                .bidId(bid.getId())
                .rideRequestId(rideRequest.getId())
                .price(bid.getPrice())
                .driverId(driver.getId())
                .driverName(driver.getUser().getPhone()) // TODO: Add real name to User entity
                .driverRating(driver.getRating())
                .motorcycleModel(motorcycle.getModel())
                .build();

        // Send to customer
        messagingTemplate.convertAndSendToUser(
                rideRequest.getCustomer().getPhone(),
                "/queue/bids",
                notification
        );
    }
}
