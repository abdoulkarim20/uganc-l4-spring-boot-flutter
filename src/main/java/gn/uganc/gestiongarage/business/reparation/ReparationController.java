package gn.uganc.gestiongarage.business.reparation;

import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;
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
@RequestMapping("/api/reparations")
public class ReparationController {

    private final IReparation reparationService;

    public ReparationController(IReparation reparationService) {
        this.reparationService = reparationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReparationDto create(@RequestBody ReparationDto reparationDto) {
        return reparationService.create(reparationDto);
    }

    @GetMapping
    public List<ReparationDto> getAll() {
        return reparationService.getAll();
    }

    @GetMapping("/{id}")
    public ReparationDto getById(@PathVariable Long id) {
        return reparationService.getById(id);
    }

    @PutMapping("/{id}")
    public ReparationDto update(@PathVariable Long id, @RequestBody ReparationDto reparationDto) {
        return reparationService.update(id, reparationDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reparationService.delete(id);
    }
}
