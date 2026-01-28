package co.zw.ehailing.ehailing.bootstrap;

import co.zw.ehailing.ehailing.enums.UserRole;
import co.zw.ehailing.ehailing.enums.VehicleType;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Motorcycle;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.MotorcycleRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Optional: clear existing data to ensure Harare data is loaded
        // Uncomment the next lines if you want to force a reload on every restart
        // motorcycleRepository.deleteAll();
        // driverRepository.deleteAll();
        // userRepository.deleteByRole(UserRole.DRIVER);

        if (driverRepository.count() > 0) {
            System.out.println("Drivers already loaded. Checking if we need to supplement...");
            // For this task, we assume if drivers exist, they might be the wrong ones, 
            // so we will clear and reload to ensure Harare data.
            System.out.println("Clearing old driver data to ensure Harare drivers are loaded...");
            motorcycleRepository.deleteAll();
            driverRepository.deleteAll();
            userRepository.deleteByRole(UserRole.DRIVER);
        }

        System.out.println("Loading initial driver data for Harare...");
        
        // Harare Central Coordinates
        double baseLat = -17.824858;
        double baseLng = 31.053028;
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            // Create User
            String phone = String.format("077%07d", i); // e.g., 0770000000
            User user = User.builder()
                    .phone(phone)
                    .password(passwordEncoder.encode("password"))
                    .role(UserRole.DRIVER)
                    .status("Active")
                    .build();
            userRepository.save(user);

            // Create Driver
            // Random position within ~5km (approx 0.05 degrees)
            // -0.05 to +0.05 variation
            double latOffset = (random.nextDouble() * 0.1) - 0.05; 
            double lngOffset = (random.nextDouble() * 0.1) - 0.05;

            Driver driver = Driver.builder()
                    .user(user)
                    .licenseNumber("LIC-" + (1000 + i))
                    .rating(4.0 + random.nextDouble()) // 4.0 to 5.0
                    .walletBalance(BigDecimal.valueOf(100.00))
                    .isOnline(true)
                    .currentLat(baseLat + latOffset)
                    .currentLng(baseLng + lngOffset)
                    .build();
            driverRepository.save(driver);

            // Create Motorcycle
            VehicleType type = random.nextBoolean() ? VehicleType.BIKE : VehicleType.KEKE;
            Motorcycle motorcycle = Motorcycle.builder()
                    .driver(driver)
                    .plateNumber("AEZ-" + (1000 + i))
                    .model(type == VehicleType.BIKE ? "Honda Ace" : "TVS King")
                    .color(randomColor(random))
                    .vehicleType(type)
                    .build();
            motorcycleRepository.save(motorcycle);
        }
        
        System.out.println("Successfully loaded 100 drivers.");
    }

    private String randomColor(Random random) {
        String[] colors = {"Red", "Blue", "Black", "White", "Yellow", "Silver"};
        return colors[random.nextInt(colors.length)];
    }
}
