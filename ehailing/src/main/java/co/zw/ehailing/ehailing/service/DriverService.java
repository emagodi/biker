package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.dto.RegisterMotorcycleRequest;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.model.Motorcycle;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import co.zw.ehailing.ehailing.repository.MotorcycleRepository;
import co.zw.ehailing.ehailing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final MotorcycleRepository motorcycleRepository;

    @Transactional
    public void updateLocation(String userPhone, Double lat, Double lng) {
        var user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));
        
        driver.setCurrentLat(lat);
        driver.setCurrentLng(lng);
        driverRepository.save(driver);
    }

    @Transactional
    public Motorcycle registerOrUpdateMotorcycle(String userPhone, RegisterMotorcycleRequest request) {
        var user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        var existing = motorcycleRepository.findByDriverId(driver.getId()).orElse(null);
        if (existing == null) {
            existing = Motorcycle.builder()
                    .driver(driver)
                    .plateNumber(request.getPlateNumber())
                    .model(request.getModel())
                    .color(request.getColor())
                    .photoUrl(request.getPhotoUrl())
                    .vehicleType(request.getVehicleType())
                    .build();
        } else {
            existing.setPlateNumber(request.getPlateNumber());
            existing.setModel(request.getModel());
            existing.setColor(request.getColor());
            existing.setPhotoUrl(request.getPhotoUrl());
            existing.setVehicleType(request.getVehicleType());
        }
        return motorcycleRepository.save(existing);
    }
}
