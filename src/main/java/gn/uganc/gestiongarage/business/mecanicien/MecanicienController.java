package gn.uganc.gestiongarage.business.mecanicien;

import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;
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
@RequestMapping("/api/mecaniciens")
public class MecanicienController {

    private final IMecanicien mecanicienService;

    public MecanicienController(IMecanicien mecanicienService) {
        this.mecanicienService = mecanicienService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MecanicienDto create(@RequestBody MecanicienDto mecanicienDto) {
        return mecanicienService.create(mecanicienDto);
    }

    @GetMapping
    public List<MecanicienDto> getAll() {
        return mecanicienService.getAll();
    }

    @GetMapping("/{id}")
    public MecanicienDto getById(@PathVariable Long id) {
        return mecanicienService.getById(id);
    }

    @PutMapping("/{id}")
    public MecanicienDto update(@PathVariable Long id, @RequestBody MecanicienDto mecanicienDto) {
        return mecanicienService.update(id, mecanicienDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        mecanicienService.delete(id);
    }
}
