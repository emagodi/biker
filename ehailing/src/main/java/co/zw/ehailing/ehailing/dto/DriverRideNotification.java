package co.zw.ehailing.ehailing.dto;

import co.zw.ehailing.ehailing.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRideNotification {
    private Long rideRequestId;
    private Double pickupLat;
    private Double pickupLng;
    private Double destLat;
    private Double destLng;
    private Double distance;
    private Double suggestedPrice;
    private Double surgeMultiplier;
    private VehicleType requestedType;
}
