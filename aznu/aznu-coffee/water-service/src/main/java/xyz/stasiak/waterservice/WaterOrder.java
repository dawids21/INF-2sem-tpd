package xyz.stasiak.waterservice;

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
public class WaterOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private UUID brewId;
    private int volume;
    private int temperature;
    private Status status;

    protected WaterOrder() {
    }

    public WaterOrder(UUID brewId, int volume, int temperature) {
        this.brewId = brewId;
        this.volume = volume;
        this.temperature = temperature;
        this.status = Status.REQUESTED;
    }

    public void prepare() throws InterruptedException {
        if (status == Status.CANCELLED || status == Status.PREPARED) {
            return;
        }
        if (temperature > 100) {
            throw new WaterException(brewId, "Water temperature is too high");
        }
        log.info("Preparing water for brew {}", brewId);
        log.info("Pouring {} ml of water", volume);
        Thread.sleep(2L * volume);
        log.info("Heating water to {} degrees", temperature);
        Thread.sleep(5L * temperature);
        this.status = Status.PREPARED;
    }

    public void cancel() {
        if (status == Status.CANCELLED) {
            return;
        }
        log.info("Cancelling water for brew {}", brewId);
        this.status = Status.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterOrder that = (WaterOrder) o;
        return volume == that.volume && temperature == that.temperature && Objects.equals(id, that.id) && Objects.equals(brewId, that.brewId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brewId, volume, temperature, status);
    }

    public enum Status {
        REQUESTED,
        PREPARED,
        CANCELLED
    }
}
