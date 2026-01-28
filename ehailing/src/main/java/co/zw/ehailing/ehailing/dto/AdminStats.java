package co.zw.ehailing.ehailing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStats {
    private long totalUsers;
    private long totalDrivers;
    private long totalRides;
    private BigDecimal totalRevenue; // Simple sum of commissions
}
