package co.zw.ehailing.ehailing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitRatingRequest {
    private Long rideId;
    private Integer score; // 1-5
    private String comment;
}
