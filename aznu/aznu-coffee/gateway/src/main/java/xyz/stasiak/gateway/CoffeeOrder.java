package xyz.stasiak.gateway;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Slf4j
public class CoffeeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private UUID orderId;
    private Integer beansWeight;
    private String beansName;
    private Integer waterVolume;
    private Integer waterTemperature;
    private Integer timeOfBrewing;
    private Status brewStatus;
    private Status waterStatus;
    private Status beansStatus;
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Set<String> reasons = new HashSet<>();

    protected CoffeeOrder() {
    }

    public CoffeeOrder(UUID orderId, String beansName, int waterTemperature) {
        this.orderId = orderId;
        this.beansName = beansName;
        this.waterTemperature = waterTemperature;
        this.brewStatus = Status.PENDING;
        this.waterStatus = Status.PENDING;
        this.beansStatus = Status.PENDING;
    }

    public void setWaterInProgress() {
        if (this.waterStatus == Status.READY) {
            throw new IllegalStateException("() (" + orderId + ") Water is already ready");
        }
        if (this.waterStatus == Status.IN_PROGRESS || this.waterStatus == Status.CANCELLED) {
            log.warn("() ({}) Water is already in progress or cancelled", orderId);
            return;
        }
        this.waterStatus = Status.IN_PROGRESS;
    }

    public void setWaterReady(int waterVolume) {
        if (this.waterStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Water is not in progress");
        }
        if (this.waterStatus == Status.READY || this.waterStatus == Status.CANCELLED) {
            log.warn("() ({}) Water is already ready or cancelled", orderId);
            return;
        }
        this.waterVolume = waterVolume;
        this.waterStatus = Status.READY;
    }

    public void setWaterCancelled(String reason) {
        if (this.waterStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Water is not in progress");
        }
        if (this.waterStatus == Status.CANCELLED) {
            log.warn("() ({}) Water is already cancelled", orderId);
            return;
        }
        reasons.add(reason);
        this.waterStatus = Status.CANCELLED;
    }

    public void setBeansInProgress() {
        if (this.beansStatus == Status.READY) {
            throw new IllegalStateException("() (" + orderId + ") Beans are already ready");
        }
        if (this.beansStatus == Status.IN_PROGRESS || this.beansStatus == Status.CANCELLED) {
            log.warn("() ({}) Beans are already in progress or cancelled", orderId);
            return;
        }
        this.beansStatus = Status.IN_PROGRESS;
    }

    public void setBeansReady(int beansWeight) {
        if (this.beansStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Beans are not in progress");
        }
        if (this.beansStatus == Status.READY || this.beansStatus == Status.CANCELLED) {
            log.warn("() ({}) Beans are already ready or cancelled", orderId);
            return;
        }
        this.beansWeight = beansWeight;
        this.beansStatus = Status.READY;
    }

    public void setBeansCancelled(String reason) {
        if (this.beansStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Beans are not in progress");
        }
        if (this.beansStatus == Status.CANCELLED) {
            log.warn("() ({}) Beans are already cancelled", orderId);
            return;
        }
        reasons.add(reason);
        this.beansStatus = Status.CANCELLED;
    }

    public void setBrewInProgress(int timeOfBrewing) {
        if (this.brewStatus == Status.READY) {
            throw new IllegalStateException("() (" + orderId + ") Brew is already ready");
        }
        if (this.brewStatus == Status.IN_PROGRESS || this.brewStatus == Status.CANCELLED) {
            log.warn("() ({}) Brew is already in progress or cancelled", orderId);
            return;
        }
        this.timeOfBrewing = timeOfBrewing;
        this.brewStatus = Status.IN_PROGRESS;
    }

    public void setBrewReady() {
        if (this.brewStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Brew is not in progress");
        }
        if (this.brewStatus == Status.READY || this.brewStatus == Status.CANCELLED) {
            log.warn("() ({}) Brew is already ready or cancelled", orderId);
            return;
        }
        this.brewStatus = Status.READY;
    }

    public void setBrewCancelled() {
        if (this.brewStatus == Status.PENDING) {
            throw new IllegalStateException("() (" + orderId + ") Brew is not in progress");
        }
        if (this.brewStatus == Status.CANCELLED) {
            log.warn("() ({}) Brew is already cancelled", orderId);
            return;
        }
        this.brewStatus = Status.CANCELLED;
    }

    public Status getStatus() {
        if (this.brewStatus == Status.CANCELLED) {
            return Status.CANCELLED;
        }
        if (this.waterStatus == Status.CANCELLED || this.beansStatus == Status.CANCELLED) {
            return Status.CANCELLED;
        }
        if (this.brewStatus == Status.READY) {
            return Status.READY;
        }
        if (this.waterStatus == Status.PENDING && this.beansStatus == Status.PENDING) {
            return Status.PENDING;
        }
        return Status.IN_PROGRESS;
    }

    public boolean isWaterAndBeansReady() {
        return waterStatus == Status.READY && beansStatus == Status.READY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeOrder that = (CoffeeOrder) o;
        return beansWeight == that.beansWeight && waterVolume == that.waterVolume && waterTemperature == that.waterTemperature && Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(beansName, that.beansName) && brewStatus == that.brewStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, beansWeight, beansName, waterVolume, waterTemperature, brewStatus);
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        READY,
        CANCELLED
    }
}
