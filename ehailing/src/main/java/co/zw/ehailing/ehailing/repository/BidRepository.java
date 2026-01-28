package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByRideRequestId(Long rideRequestId);
}
