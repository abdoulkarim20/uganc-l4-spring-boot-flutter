package gn.uganc.gestiongarage.business.utilisateur;

import gn.uganc.gestiongarage.business.utilisateur.dtos.UtilisateurDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final IUtilisateur utilisateurService;

    public UtilisateurController(IUtilisateur utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurDto create(@RequestBody UtilisateurDto utilisateurDto) {
        return utilisateurService.create(utilisateurDto);
    }

    @GetMapping
    public List<UtilisateurDto> getAll() {
        return utilisateurService.getAll();
    }

    @GetMapping("/{id}")
    public UtilisateurDto getById(@PathVariable Long id) {
        return utilisateurService.getById(id);
    }

    @PutMapping("/{id}")
    public UtilisateurDto update(@PathVariable Long id, @RequestBody UtilisateurDto utilisateurDto) {
        return utilisateurService.update(id, utilisateurDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        utilisateurService.delete(id);
    }
}
