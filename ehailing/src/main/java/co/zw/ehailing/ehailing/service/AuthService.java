package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.AuthResponse;
import co.zw.ehailing.ehailing.dto.LoginRequest;
import co.zw.ehailing.ehailing.dto.RegisterRequest;
import co.zw.ehailing.ehailing.enums.DriverStatus;
import co.zw.ehailing.ehailing.enums.UserRole;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Motorcycle;
import co.zw.ehailing.ehailing.model.User;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.MotorcycleRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status("PENDING") // Default status
                .build();
        
        // If it's a driver, we might set status to PENDING, otherwise ACTIVE?
        // Req says "Driver signs up -> Account status = PENDING".
        // Customer signs up -> usually ACTIVE immediately.
        if (request.getRole() == UserRole.CUSTOMER) {
            user.setStatus("ACTIVE");
        } else {
            user.setStatus("PENDING");
        }

        var savedUser = userRepository.save(user);

        if (request.getRole() == UserRole.DRIVER) {
            var driver = Driver.builder()
                    .user(savedUser)
                    .licenseNumber(request.getLicenseNumber())
                    .rating(5.0) // Start with 5 stars? Or 0?
                    .walletBalance(BigDecimal.ZERO)
                    .isOnline(false)
                    .build();
            var savedDriver = driverRepository.save(driver);

            if (request.getPlateNumber() != null) {
                var motorcycle = Motorcycle.builder()
                        .driver(savedDriver)
                        .plateNumber(request.getPlateNumber())
                        .model(request.getModel())
                        .color(request.getColor())
                        .build();
                motorcycleRepository.save(motorcycle);
            }
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByPhone(request.getPhone())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }
}
