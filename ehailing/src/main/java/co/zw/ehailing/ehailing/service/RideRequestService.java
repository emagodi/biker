package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.CreateRideRequest;
import co.zw.ehailing.ehailing.dto.DriverRideNotification;
import co.zw.ehailing.ehailing.enums.RideRequestStatus;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.RideRequest;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
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
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final double BASE_FARE = 2.0;
    private static final double PER_KM_RATE = 1.5;
    private static final double SEARCH_RADIUS_KM = 10.0; // Driver search radius

    @Transactional
    public RideRequest createRideRequest(CreateRideRequest request, String userPhone) {
        User customer = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Calculate distance (Stub implementation)
        // In real world, use Google Maps API / Mapbox
        double distance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDestLat(), request.getDestLng());

        // Calculate Surge & Price
        // Fetch active drivers
        List<Driver> activeDrivers = driverRepository.findAll().stream()
                 .filter(d -> Boolean.TRUE.equals(d.getIsOnline()))
                 .toList();
        
        double surge = calculateSurgeMultiplier(activeDrivers.size());
        double suggestedPrice = (BASE_FARE + (distance * PER_KM_RATE)) * surge;

        RideRequest rideRequest = RideRequest.builder()
                .customer(customer)
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .destLat(request.getDestLat())
                .destLng(request.getDestLng())
                .distance(distance)
                .status(RideRequestStatus.REQUESTED)
                .surgeMultiplier(surge)
                .suggestedPrice(suggestedPrice)
                .build();

        RideRequest savedRequest = rideRequestRepository.save(rideRequest);

        // Find nearby drivers and notify them
        notifyNearbyDrivers(savedRequest, activeDrivers);

        return savedRequest;
    }

    private double calculateSurgeMultiplier(int activeDrivers) {
        if (activeDrivers == 0) return 2.0;
        if (activeDrivers < 5) return 1.5;
        if (activeDrivers < 10) return 1.2;
        return 1.0;
    }

    private void notifyNearbyDrivers(RideRequest rideRequest, List<Driver> activeDrivers) {
        // Filter by Radius, Status & Wallet
        List<Driver> eligibleDrivers = activeDrivers.stream()
                .filter(walletService::hasSufficientCredit) // Must have credit
                .filter(d -> {
                    if (d.getCurrentLat() == null || d.getCurrentLng() == null) {
                        return false; // Driver location unknown
                    }
                    double dist = calculateDistance(d.getCurrentLat(), d.getCurrentLng(),
                            rideRequest.getPickupLat(), rideRequest.getPickupLng());
                    return dist <= SEARCH_RADIUS_KM;
                })
                .toList();
        
        DriverRideNotification notification = DriverRideNotification.builder()
                .rideRequestId(rideRequest.getId())
                .pickupLat(rideRequest.getPickupLat())
                .pickupLng(rideRequest.getPickupLng())
                .destLat(rideRequest.getDestLat())
                .destLng(rideRequest.getDestLng())
                .distance(rideRequest.getDistance())
                .suggestedPrice(rideRequest.getSuggestedPrice())
                .surgeMultiplier(rideRequest.getSurgeMultiplier())
                .build();

        for (Driver driver : eligibleDrivers) {
            // Send to specific driver via WebSocket
            // Assuming driver subscribes to /user/{phone}/queue/ride-requests
            messagingTemplate.convertAndSendToUser(
                    driver.getUser().getPhone(),
                    "/queue/ride-requests",
                    notification
            );
        }
        
        // Update status to BIDDING after notifying
        rideRequest.setStatus(RideRequestStatus.BIDDING);
        rideRequestRepository.save(rideRequest);
    }

    public List<RideRequest> getOpenRequests() {
        return rideRequestRepository.findByStatus(RideRequestStatus.REQUESTED);
    }

    // Haversine formula for distance calculation (km)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
