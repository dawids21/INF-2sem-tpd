package xyz.stasiak.brewservice;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrewOrderRepository extends JpaRepository<BrewOrder, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BrewOrder b WHERE b.brewId = :brewId")
    Optional<BrewOrder> findByBrewIdForUpdate(UUID brewId);

    Optional<BrewOrder> findByBrewId(UUID brewId);

}
