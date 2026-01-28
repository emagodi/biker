package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.AdminStats;
import co.zw.ehailing.ehailing.enums.UserRole;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Ride;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.RideRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import co.zw.ehailing.ehailing.enums.RideStatus;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    @Transactional
    public void approveDriver(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != UserRole.DRIVER) {
             throw new RuntimeException("User is not a driver");
        }
        
        user.setStatus("ACTIVE");
        userRepository.save(user);
    }

    @Transactional
    public void rejectDriver(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != UserRole.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        user.setStatus("REJECTED");
        userRepository.save(user);
    }

    public List<Driver> getPendingDrivers() {
        // Need to join with User table to check status = 'PENDING'
        // Or simpler: Find all users with role DRIVER and status PENDING, then find their Driver profiles
        // For MVP, let's just return all drivers and let frontend filter, or add custom query.
        // Let's add a custom query in DriverRepository if needed. 
        // Or stream filter here (less efficient but fine for MVP).
        return driverRepository.findAll().stream()
                .filter(d -> "PENDING".equals(d.getUser().getStatus()))
                .toList();
    }

    public AdminStats getStats() {
        long totalUsers = userRepository.count();
        long totalDrivers = driverRepository.count();
        List<Ride> allRides = rideRepository.findAll();
        long totalRides = allRides.size();
        
        // Calculate Revenue (Assuming 10% commission on all completed rides)
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal commissionRate = new BigDecimal("0.10");
        
        for (Ride ride : allRides) {
            if (ride.getStatus() == RideStatus.COMPLETED && ride.getFinalPrice() != null) {
                 totalRevenue = totalRevenue.add(ride.getFinalPrice().multiply(commissionRate));
            }
        }

        return AdminStats.builder()
                .totalUsers(totalUsers)
                .totalDrivers(totalDrivers)
                .totalRides(totalRides)
                .totalRevenue(totalRevenue)
                .build();
    }
}
