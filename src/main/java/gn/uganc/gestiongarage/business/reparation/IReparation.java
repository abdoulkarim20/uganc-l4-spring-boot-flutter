package gn.uganc.gestiongarage.business.reparation;

import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;

import java.util.List;

public interface IReparation {

    ReparationDto create(ReparationDto reparationDto);

    List<ReparationDto> getAll();

    ReparationDto getById(Long id);

    ReparationDto update(Long id, ReparationDto reparationDto);

    void delete(Long id);
}
