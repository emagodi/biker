package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.enums.RideRequestStatus;
import co.zw.ehailing.ehailing.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByStatus(RideRequestStatus status);
}
