package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.RideRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideHistoryService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public List<Ride> getUserRideHistory(String userPhone) {
        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // If user is driver, return drives. If passenger, return rides.
        // Or return both? Usually app separates 'My Rides' (as passenger) and 'Trip History' (as driver).
        // Let's return based on what they are.
        
        // Simple logic: Combine both? Or check role?
        // Let's check role or just query both and merge (if a user can be both, though roles suggest separation).
        // Assuming strict role separation for now based on 'UserRole'.
        
        switch (user.getRole()) {
            case DRIVER:
                Driver driver = driverRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Driver profile not found"));
                return rideRepository.findByDriverId(driver.getId());
            case CUSTOMER:
                return rideRepository.findByCustomerId(user.getId());
            default:
                return Collections.emptyList();
        }
    }
}
