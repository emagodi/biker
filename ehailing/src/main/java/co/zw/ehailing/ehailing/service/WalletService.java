package co.zw.ehailing.ehailing.service;

import co.zw.ehailing.ehailing.model.Driver;
import co.zw.ehailing.ehailing.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final DriverRepository driverRepository;
    
    // Minimum balance required to receive ride requests
    private static final BigDecimal MIN_BALANCE_THRESHOLD = BigDecimal.ZERO; 

    @Transactional
    public void topUpWallet(Long driverId, BigDecimal amount) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        driver.setWalletBalance(driver.getWalletBalance().add(amount));
        driverRepository.save(driver);
    }

    // Commission percentage (e.g. 10%)
    private static final BigDecimal COMMISSION_PERCENTAGE = new BigDecimal("0.10");

    @Transactional
    public void deductCommission(Long driverId, BigDecimal ridePrice) {
        BigDecimal commission = ridePrice.multiply(COMMISSION_PERCENTAGE);
        deductFunds(driverId, commission);
    }

    @Transactional
    public void deductFunds(Long driverId, BigDecimal amount) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        if (driver.getWalletBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        driver.setWalletBalance(driver.getWalletBalance().subtract(amount));
        driverRepository.save(driver);
    }

    public boolean hasSufficientCredit(Driver driver) {
        return driver.getWalletBalance().compareTo(MIN_BALANCE_THRESHOLD) > 0;
    }
}
