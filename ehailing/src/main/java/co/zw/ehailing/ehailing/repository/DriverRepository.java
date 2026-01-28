package co.zw.ehailing.ehailing.repository;

import co.zw.ehailing.ehailing.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUserId(Long userId);
    // Find active drivers logic will go here later
}
