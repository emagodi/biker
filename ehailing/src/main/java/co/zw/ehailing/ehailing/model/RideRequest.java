package co.zw.ehailing.ehailing.model;

import co.zw.ehailing.ehailing.enums.RideRequestStatus;
import co.zw.ehailing.ehailing.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ride_requests")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "pickup_lat", nullable = false)
    private Double pickupLat;

    @Column(name = "pickup_lng", nullable = false)
    private Double pickupLng;

    @Column(name = "dest_lat", nullable = false)
    private Double destLat;

    @Column(name = "dest_lng", nullable = false)
    private Double destLng;

    private Double distance; // in km

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status;

    @Column(name = "surge_multiplier")
    private Double surgeMultiplier; // e.g. 1.0, 1.5, 2.0

    @Column(name = "suggested_price")
    private Double suggestedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_type")
    private VehicleType requestedType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
