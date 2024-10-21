package xyz.stasiak.coffeeui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/coffee")
@RequiredArgsConstructor
@Slf4j
public class CoffeeUiController {

    private final RestTemplate restTemplate;

    @Value("${coffee.gateway.url}")
    private String coffeeGatewayUrl;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("coffeeMakeRequest", new CoffeeMakeRequest(null, null));
        return "make";
    }

    @PostMapping
    public String make(CoffeeMakeRequest coffeeMakeRequest, Model model) {
        try {
            ResponseEntity<CoffeeMakeResponse> response = restTemplate.postForEntity(
                    coffeeGatewayUrl + "/api/coffee/make", coffeeMakeRequest,
                    CoffeeMakeResponse.class);
            UUID brewId = response.getBody().brewId();
            return "redirect:/coffee/status/" + brewId.toString();
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            model.addAttribute("problemDetail", problemDetail);
        }
        model.addAttribute("coffeeMakeRequest", coffeeMakeRequest);
        return "make";
    }

    @GetMapping("/status/{brewId}")
    public String status(@PathVariable UUID brewId, Model model) {
        try {
            ResponseEntity<CoffeeOrderResponse> response = restTemplate.getForEntity(
                    coffeeGatewayUrl + "/api/coffee/status/" + brewId.toString(),
                    CoffeeOrderResponse.class);
            model.addAttribute("coffeeOrderResponse", response.getBody());
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            model.addAttribute("problemDetail", problemDetail);
            return "make";
        }
        model.addAttribute("statusColorMap", getStatusColorMap());
        return "result";
    }

    public Map<String, String> getStatusColorMap() {
        return Map.of(
                "Pending", "text-warning",
                "In progress", "text-primary",
                "Ready", "text-success",
                "Cancelled", "text-danger"
        );
    }
}
