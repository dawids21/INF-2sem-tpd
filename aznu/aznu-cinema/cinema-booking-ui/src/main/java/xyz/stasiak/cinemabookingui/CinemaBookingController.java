package xyz.stasiak.cinemabookingui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import xyz.stasiak.cinemabooking.ReservationRequest;

@Controller
public class CinemaBookingController {

    @GetMapping("/bookSeats")
    public String bookSeatsForm(Model model) {
        return "";
    }


    @PostMapping("/bookSeats")
    public String bookSeats(@ModelAttribute ReservationRequest brf, Model model) {
        return "";
    }

}
