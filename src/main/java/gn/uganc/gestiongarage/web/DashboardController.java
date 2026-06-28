package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String landing() {
        return "forward:/landing.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/client/access")
    public String clientAccess() {
        return "forward:/client-access.html";
    }

    @GetMapping("/client/register")
    public String clientRegister() {
        return "forward:/client-register.html";
    }

    @GetMapping("/garage/access")
    public String garageAccess() {
        return "forward:/garage-access.html";
    }

    @GetMapping("/vehicle/health")
    public String vehicleHealth() {
        return "forward:/vehicle-health.html";
    }
}
