package gn.uganc.gestiongarage.business.vehicule;

import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;

import java.util.List;

public interface IVehicule {

    VehiculeDto create(VehiculeDto vehiculeDto);

    List<VehiculeDto> getAll();

    VehiculeDto getById(Long id);

    VehiculeDto update(Long id, VehiculeDto vehiculeDto);

    void delete(Long id);
}
