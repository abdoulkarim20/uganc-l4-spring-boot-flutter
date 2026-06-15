package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientSpaceWebController {

    @GetMapping("/client/dashboard")
    public String dashboard() {
        return "forward:/client-dashboard.html";
    }
}
