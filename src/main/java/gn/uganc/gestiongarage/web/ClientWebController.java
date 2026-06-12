package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientWebController {

    @GetMapping({"/clients", "/clients/create", "/clients/{id}/edit"})
    public String clients() {
        return "forward:/crud.html";
    }
}
