package xyz.stasiak.brewservice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Slf4j
public class BrewOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private UUID brewId;
    private int waterVolume;
    private int beansWeight;
    private Status status;

    protected BrewOrder() {
    }

    public BrewOrder(UUID brewId, int waterVolume, int beansWeight) {
        this.brewId = brewId;
        this.waterVolume = waterVolume;
        this.beansWeight = beansWeight;
        this.status = Status.REQUESTED;
    }

    public int startBrew() {
        if (status == Status.BREWING || status == Status.CANCELLED || status == Status.FINISHED) {
            return 0;
        }
        log.info("Calculating brew time for brew {}", brewId);
        int brewTime = 10 * waterVolume + 200 * beansWeight;
        log.info("Brew time for brew {} is {} milliseconds", brewId, brewTime);
        this.status = Status.BREWING;
        return brewTime;
    }

    public void finish() {
        if (status == Status.REQUESTED) {
            throw new BrewException(brewId, "Brew not started yet");
        }
        if (status == Status.FINISHED || status == Status.CANCELLED) {
            return;
        }
        log.info("Finishing brew {}", brewId);
        this.status = Status.FINISHED;
    }

    public void cancel() {
        if (status == Status.CANCELLED) {
            return;
        }
        log.info("Cancelling brew {}", brewId);
        this.status = Status.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrewOrder that = (BrewOrder) o;
        return waterVolume == that.waterVolume && beansWeight == that.beansWeight && Objects.equals(id, that.id) && Objects.equals(brewId, that.brewId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brewId, waterVolume, beansWeight, status);
    }

    public enum Status {
        REQUESTED,
        BREWING,
        FINISHED,
        CANCELLED
    }
}
