package gn.uganc.gestiongarage.business.mecanicien.mappers;

import gn.uganc.gestiongarage.business.mecanicien.Mecanicien;
import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;
import org.springframework.stereotype.Component;

@Component
public class MecanicienMapper {

    public MecanicienDto toDto(Mecanicien mecanicien) {
        MecanicienDto mecanicienDto = new MecanicienDto();
        mecanicienDto.setId(mecanicien.getId());
        mecanicienDto.setNom(mecanicien.getNom());
        mecanicienDto.setPrenom(mecanicien.getPrenom());
        mecanicienDto.setTelephone(mecanicien.getTelephone());
        mecanicienDto.setSpecialite(mecanicien.getSpecialite());
        return mecanicienDto;
    }

    public Mecanicien toEntity(MecanicienDto mecanicienDto) {
        Mecanicien mecanicien = new Mecanicien();
        mecanicien.setId(mecanicienDto.getId());
        mecanicien.setNom(mecanicienDto.getNom());
        mecanicien.setPrenom(mecanicienDto.getPrenom());
        mecanicien.setTelephone(mecanicienDto.getTelephone());
        mecanicien.setSpecialite(mecanicienDto.getSpecialite());
        return mecanicien;
    }
}
