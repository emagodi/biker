package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.dto.RegisterMotorcycleRequest;
import co.zw.ehailing.ehailing.dto.UpdateLocationRequest;
import co.zw.ehailing.ehailing.model.Motorcycle;
import co.zw.ehailing.ehailing.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(
            @RequestBody UpdateLocationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        driverService.updateLocation(userDetails.getUsername(), request.getLat(), request.getLng());
        return ResponseEntity.ok("Location updated");
    }

    @PostMapping("/motorcycle")
    public ResponseEntity<Motorcycle> registerMotorcycle(
            @RequestBody RegisterMotorcycleRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(driverService.registerOrUpdateMotorcycle(userDetails.getUsername(), request));
    }
}
