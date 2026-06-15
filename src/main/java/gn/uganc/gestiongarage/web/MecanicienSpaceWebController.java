package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MecanicienSpaceWebController {

    @GetMapping("/mecanicien/dashboard")
    public String dashboard() {
        return "forward:/mecanicien-dashboard.html";
    }
}
