package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MecanicienWebController {

    @GetMapping({"/mecaniciens", "/mecaniciens/create", "/mecaniciens/{id}/edit"})
    public String mecaniciens() {
        return "forward:/crud.html";
    }

    @GetMapping("/mecaniciens/{id}")
    public String mecanicienDetail() {
        return "forward:/detail.html";
    }
}
