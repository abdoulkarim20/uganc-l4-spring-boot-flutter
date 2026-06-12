package gn.uganc.gestiongarage.business.mecanicien;

import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;

import java.util.List;

public interface IMecanicien {

    MecanicienDto create(MecanicienDto mecanicienDto);

    List<MecanicienDto> getAll();

    MecanicienDto getById(Long id);

    MecanicienDto update(Long id, MecanicienDto mecanicienDto);

    void delete(Long id);
}
