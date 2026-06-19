package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GarageWebController {

    @GetMapping({"/garages", "/garages/create", "/garages/{id}/edit"})
    public String garages() {
        return "forward:/crud.html";
    }

    @GetMapping("/garages/{id}")
    public String garageDetail() {
        return "forward:/detail.html";
    }
}
