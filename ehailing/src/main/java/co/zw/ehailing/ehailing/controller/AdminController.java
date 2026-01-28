package co.zw.ehailing.ehailing.controller;

import co.zw.ehailing.ehailing.dto.AdminStats;
import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Need to configure PreAuthorize or simple check in service
    // For now, assuming anyone with ADMIN role can access via SecurityConfig (we need to update SecurityConfig)
    
    @PostMapping("/approve-driver/{userId}")
    public ResponseEntity<String> approveDriver(@PathVariable Long userId) {
        adminService.approveDriver(userId);
        return ResponseEntity.ok("Driver approved");
    }

    @PostMapping("/reject-driver/{userId}")
    public ResponseEntity<String> rejectDriver(@PathVariable Long userId) {
        adminService.rejectDriver(userId);
        return ResponseEntity.ok("Driver rejected");
    }

    @GetMapping("/pending-drivers")
    public ResponseEntity<List<Driver>> getPendingDrivers() {
        return ResponseEntity.ok(adminService.getPendingDrivers());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
