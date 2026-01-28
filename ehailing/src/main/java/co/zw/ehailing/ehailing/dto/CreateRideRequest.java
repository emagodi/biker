package co.zw.ehailing.ehailing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequest {
    private Double pickupLat;
    private Double pickupLng;
    private Double destLat;
    private Double destLng;
}
