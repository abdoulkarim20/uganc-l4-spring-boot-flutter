package gn.uganc.gestiongarage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UtilisateurWebController {

    @GetMapping({"/utilisateurs", "/utilisateurs/create", "/utilisateurs/{id}/edit"})
    public String utilisateurs() {
        return "forward:/crud.html";
    }

    @GetMapping("/utilisateurs/{id}")
    public String utilisateurDetail() {
        return "forward:/detail.html";
    }
}
