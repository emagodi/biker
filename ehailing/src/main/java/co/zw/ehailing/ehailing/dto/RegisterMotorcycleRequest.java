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
public class RegisterMotorcycleRequest {
    private String plateNumber;
    private String model;
    private String color;
    private String photoUrl;
    private VehicleType vehicleType;
}
