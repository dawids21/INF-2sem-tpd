package org.bp.paymentclient;

import org.bp.paymentclient.model.PaymentRequest;
import org.bp.paymentclient.model.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class PaymentController {
    private final RestTemplate restTemplate;

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/pay")
    public String payForm(Model model) {
        model.addAttribute("paymentRequest", new PaymentRequest());
        return "pay";
    }


    @PostMapping("/pay")
    public String pay(@ModelAttribute PaymentRequest paymentRequest, Model model) {
        try {
            ResponseEntity<PaymentResponse> re = restTemplate.postForEntity(
                    "http://localhost:8083/payment", paymentRequest,
                    PaymentResponse.class);
            model.addAttribute("paymentResponse", re.getBody());
            return "result";
        } catch (HttpClientErrorException e) { //catch 4xx response codes
            model.addAttribute("faultMsg", e.getMessage());
            return "fault";
        }
    }

}
