package xyz.stasiak.beanssoapservice.types;

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
public class BeansOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private UUID brewId;
    private String name;
    private int weight;
    private Status status;

    protected BeansOrder() {
    }

    public BeansOrder(UUID brewId, String name, int weight) {
        this.brewId = brewId;
        this.name = name;
        this.weight = weight;
        this.status = Status.REQUESTED;
    }

    public boolean grind() throws InterruptedException {
        if (status == Status.CANCELED || status == Status.PREPARED) {
            return false;
        }
        if (weight > 50) {
            throw new BeansSoapException(brewId, "Beans weight is too high");
        }
        log.info("Grinding {} g of {} beans for brew {}", weight, name, brewId);
        Thread.sleep(5L * weight);
        this.status = Status.PREPARED;
        return true;
    }

    public boolean cancel() {
        if (status == Status.CANCELED) {
            return false;
        }
        log.info("Cancelling beans for brew {}", brewId);
        this.status = Status.CANCELED;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeansOrder that = (BeansOrder) o;
        return weight == that.weight && Objects.equals(id, that.id) && Objects.equals(brewId, that.brewId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brewId, name, weight);
    }

    public enum Status {
        REQUESTED,
        PREPARED,
        CANCELED
    }
}
