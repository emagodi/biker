package co.zw.ehailing.ehailing.model;

import co.zw.ehailing.ehailing.enums.UserRole;
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
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer; // Who is giving the rating

    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee; // Who is receiving the rating

    @Column(nullable = false)
    private Integer score; // 1-5

    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
