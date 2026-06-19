package gn.uganc.gestiongarage.business.garage;

import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;

import java.util.List;

public interface IGarage {

    GarageDto create(GarageDto garageDto);

    List<GarageDto> getAll();

    GarageDto getById(Long id);

    GarageDto update(Long id, GarageDto garageDto);

    void delete(Long id);
}
