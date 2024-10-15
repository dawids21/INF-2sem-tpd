package xyz.stasiak.gateway;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    CoffeeOrder findByOrderId(UUID orderId);
}
