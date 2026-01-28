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
public class BidNotification {
    private Long bidId;
    private Long rideRequestId;
    private BigDecimal price;
    private Long driverId;
    private String driverName; // Using phone for now if name is not separate
    private Double driverRating;
    private String motorcycleModel;
}
