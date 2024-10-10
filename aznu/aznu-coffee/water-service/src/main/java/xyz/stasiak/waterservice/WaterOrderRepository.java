package xyz.stasiak.waterservice;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaterOrderRepository extends JpaRepository<WaterOrder, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WaterOrder w WHERE w.brewId = :brewId")
    Optional<WaterOrder> findByBrewIdForUpdate(UUID brewId);

    Optional<WaterOrder> findByBrewId(UUID brewId);

}
