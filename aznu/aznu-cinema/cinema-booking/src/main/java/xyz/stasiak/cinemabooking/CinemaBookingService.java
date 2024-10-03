package xyz.stasiak.cinemabooking;


import jakarta.jws.WebService;
import org.springframework.stereotype.Service;
import xyz.stasiak.cinemabooking.types.Fault;
import xyz.stasiak.cinemabooking.types.ReservationInfo;
import xyz.stasiak.cinemabooking.types.ReservationRequest;

import java.math.BigDecimal;

@WebService
@Service
public class CinemaBookingService {

    public ReservationInfo makeReservation(ReservationRequest reservationRequest) throws Fault {
//        if (reservationRequest != null) {
//            Flight fligth = fligthTicket.getFlight();
//            if (fligth != null && fligth.getFrom() != null && fligth.getFrom().getAirport() != null
//                    && fligth.getTo() != null && fligth.getFrom().getAirport().equals(fligth.getTo().getAirport())
//            ) {
//                Fault fault = new Fault();
//                fault.setCode(5);
//                fault.setText("Start and destination airport can not be the same");
//                throw fault;
//            }
//        }
        // TODO add validation
        ReservationInfo reservationInfo = new ReservationInfo();
        reservationInfo.setId(1);
        reservationInfo.setCost(new BigDecimal(10 * reservationRequest.getSeats().getSeat().size()));
        return reservationInfo;

    }

    public ReservationInfo cancelReservation(int reservationId) throws Fault {
        return null;
    }
}