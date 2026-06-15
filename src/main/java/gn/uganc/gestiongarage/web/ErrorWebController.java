package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorWebController {

    @GetMapping("/403")
    public String forbidden() {
        return "forward:/403.html";
    }
}
