package xyz.stasiak.coffeeui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coffee")
public class CoffeeUiController {

        @GetMapping
        public String index(Model model) {
            model.addAttribute("coffeeMakeRequest", new CoffeeMakeRequest(null, null));
            return "make";
        }

        @PostMapping
        public String make(CoffeeMakeRequest coffeeMakeRequest, Model model) {
            model.addAttribute("coffeeMakeRequest", coffeeMakeRequest);
            return "result";
        }
}
