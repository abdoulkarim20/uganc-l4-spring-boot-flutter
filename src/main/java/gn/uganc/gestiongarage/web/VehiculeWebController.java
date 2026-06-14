package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VehiculeWebController {

    @GetMapping({"/vehicules", "/vehicules/create", "/vehicules/{id}/edit"})
    public String vehicules() {
        return "forward:/crud.html";
    }

    @GetMapping("/vehicules/{id}")
    public String vehiculeDetail() {
        return "forward:/detail.html";
    }
}
