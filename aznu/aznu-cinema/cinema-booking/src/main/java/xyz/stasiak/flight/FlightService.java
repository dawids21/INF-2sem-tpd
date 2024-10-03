package xyz.stasiak.flight;


import jakarta.jws.WebService;
import xyz.stasiak.types.BookingInfo;
import xyz.stasiak.types.Fault;
import xyz.stasiak.types.Flight;
import xyz.stasiak.types.FlightTicket;

@WebService
@org.springframework.stereotype.Service
public class FlightService {

    public BookingInfo bookFligth(FlightTicket fligthTicket) throws Fault {
        if (fligthTicket != null) {
            Flight fligth = fligthTicket.getFlight();
            if (fligth != null && fligth.getFrom() != null && fligth.getFrom().getAirport() != null
                    && fligth.getTo() != null && fligth.getFrom().getAirport().equals(fligth.getTo().getAirport())
            ) {
                Fault fault = new Fault();
                fault.setCode(5);
                fault.setText("Start and destination airport can not be the same");
                throw fault;
            }
        }
        BookingInfo bookingInfo = new BookingInfo();
        bookingInfo.setId(1);
        bookingInfo.setCost(new java.math.BigDecimal(345));
        return bookingInfo;

    }

    public BookingInfo cancelBooking(int bookingId) throws Fault {
        return null;
    }
}