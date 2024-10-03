package xyz.stasiak.cinemabooking.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfo {
    private int id;
    private BigDecimal cost;
}
