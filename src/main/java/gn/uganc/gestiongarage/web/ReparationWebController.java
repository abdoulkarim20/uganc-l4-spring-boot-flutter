package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReparationWebController {

    @GetMapping({"/reparations", "/reparations/create", "/reparations/{id}/edit"})
    public String reparations() {
        return "forward:/crud.html";
    }
}
