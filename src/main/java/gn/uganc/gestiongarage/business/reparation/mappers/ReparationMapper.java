package gn.uganc.gestiongarage.business.reparation.mappers;

import gn.uganc.gestiongarage.business.mecanicien.Mecanicien;
import gn.uganc.gestiongarage.business.reparation.Reparation;
import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import org.springframework.stereotype.Component;

@Component
public class ReparationMapper {

    public ReparationDto toDto(Reparation reparation) {
        ReparationDto reparationDto = new ReparationDto();
        reparationDto.setId(reparation.getId());
        reparationDto.setDateReparation(reparation.getDateReparation());
        reparationDto.setDescription(reparation.getDescription());
        reparationDto.setCout(reparation.getCout());
        reparationDto.setStatut(reparation.getStatut());
        reparationDto.setVehiculeId(reparation.getVehicule() != null ? reparation.getVehicule().getId() : null);
        reparationDto.setMecanicienId(reparation.getMecanicien() != null ? reparation.getMecanicien().getId() : null);
        return reparationDto;
    }

    public Reparation toEntity(ReparationDto reparationDto, Vehicule vehicule, Mecanicien mecanicien) {
        Reparation reparation = new Reparation();
        reparation.setId(reparationDto.getId());
        reparation.setDateReparation(reparationDto.getDateReparation());
        reparation.setDescription(reparationDto.getDescription());
        reparation.setCout(reparationDto.getCout());
        reparation.setStatut(reparationDto.getStatut());
        reparation.setVehicule(vehicule);
        reparation.setMecanicien(mecanicien);
        return reparation;
    }
}
