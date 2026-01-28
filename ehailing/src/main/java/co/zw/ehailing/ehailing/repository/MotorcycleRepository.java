package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.model.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {
    Optional<Motorcycle> findByDriverId(Long driverId);
}
