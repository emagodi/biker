package co.zw.ehailing.ehailing.dto;

import co.zw.ehailing.ehailing.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String phone;
    private String password;
    private UserRole role;
    
    // Driver specific fields (optional in DTO, handled in service)
    private String licenseNumber;
    private String plateNumber;
    private String model;
    private String color;
}
