package xyz.stasiak.beanssoapservice;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.stasiak.beanssoapservice.types.BeansOrder;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeansOrderRepository extends JpaRepository<BeansOrder, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM BeansOrder w WHERE w.brewId = :brewId")
    Optional<BeansOrder> findByBrewIdForUpdate(UUID brewId);

    Optional<BeansOrder> findByBrewId(UUID brewId);
}
