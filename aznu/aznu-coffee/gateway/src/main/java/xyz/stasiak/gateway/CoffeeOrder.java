package xyz.stasiak.gateway;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class CoffeeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private UUID orderId;
    private int beansWeight;
    private String beansName;
    private int waterVolume;
    private int waterTemperature;
    private Status status;
    private Status waterStatus;
    private Status beansStatus;

    protected CoffeeOrder() {
    }

    public CoffeeOrder(UUID orderId, int beansWeight, String beansName, int waterVolume, int waterTemperature) {
        this.orderId = orderId;
        this.beansWeight = beansWeight;
        this.beansName = beansName;
        this.waterVolume = waterVolume;
        this.waterTemperature = waterTemperature;
        this.status = Status.PENDING;
        this.waterStatus = Status.PENDING;
        this.beansStatus = Status.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeOrder that = (CoffeeOrder) o;
        return beansWeight == that.beansWeight && waterVolume == that.waterVolume && waterTemperature == that.waterTemperature && Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(beansName, that.beansName) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, beansWeight, beansName, waterVolume, waterTemperature, status);
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        READY,
        CANCELLED
    }
}
