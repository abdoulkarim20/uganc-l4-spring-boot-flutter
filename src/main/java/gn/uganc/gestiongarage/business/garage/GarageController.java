package gn.uganc.gestiongarage.business.garage;

import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;
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
@RequestMapping("/api/garages")
public class GarageController {

    private final IGarage garageService;

    public GarageController(IGarage garageService) {
        this.garageService = garageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GarageDto create(@RequestBody GarageDto garageDto) {
        return garageService.create(garageDto);
    }

    @GetMapping
    public List<GarageDto> getAll() {
        return garageService.getAll();
    }

    @GetMapping("/{id}")
    public GarageDto getById(@PathVariable Long id) {
        return garageService.getById(id);
    }

    @PutMapping("/{id}")
    public GarageDto update(@PathVariable Long id, @RequestBody GarageDto garageDto) {
        return garageService.update(id, garageDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        garageService.delete(id);
    }
}
