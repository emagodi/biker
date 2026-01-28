package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    
    @Query("SELECT r FROM Ride r WHERE r.rideRequest.customer.id = :customerId")
    List<Ride> findByCustomerId(Long customerId);
}
