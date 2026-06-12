package gn.uganc.gestiongarage.business.vehicule;

import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;
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
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final IVehicule vehiculeService;

    public VehiculeController(IVehicule vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDto create(@RequestBody VehiculeDto vehiculeDto) {
        return vehiculeService.create(vehiculeDto);
    }

    @GetMapping
    public List<VehiculeDto> getAll() {
        return vehiculeService.getAll();
    }

    @GetMapping("/{id}")
    public VehiculeDto getById(@PathVariable Long id) {
        return vehiculeService.getById(id);
    }

    @PutMapping("/{id}")
    public VehiculeDto update(@PathVariable Long id, @RequestBody VehiculeDto vehiculeDto) {
        return vehiculeService.update(id, vehiculeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehiculeService.delete(id);
    }
}
