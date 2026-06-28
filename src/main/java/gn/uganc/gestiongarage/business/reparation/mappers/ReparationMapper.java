package gn.uganc.gestiongarage.business.reparation.mappers;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.reparation.Reparation;
import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import org.springframework.stereotype.Component;

@Component
public class ReparationMapper {

    public ReparationDto toDto(Reparation reparation) {
        ReparationDto reparationDto = new ReparationDto();
        reparationDto.setId(reparation.getId());
        reparationDto.setDateReparation(reparation.getDateReparation());
        reparationDto.setDescription(reparation.getDescription());
        reparationDto.setConsigneClient(reparation.getConsigneClient());
        reparationDto.setCout(reparation.getCout());
        reparationDto.setStatut(reparation.getStatut());
        reparationDto.setVehiculeId(reparation.getVehicule() != null ? reparation.getVehicule().getId() : null);
        reparationDto.setMecanicienId(reparation.getMecanicienUtilisateur() != null ? reparation.getMecanicienUtilisateur().getId() : null);
        if (reparation.getGarage() != null) {
            reparationDto.setGarageId(reparation.getGarage().getId());
            reparationDto.setGarageNom(reparation.getGarage().getNom());
        }
        return reparationDto;
    }

    public Reparation toEntity(ReparationDto reparationDto, Vehicule vehicule, Utilisateur mecanicien, Garage garage) {
        Reparation reparation = new Reparation();
        reparation.setId(reparationDto.getId());
        reparation.setDateReparation(reparationDto.getDateReparation());
        reparation.setDescription(reparationDto.getDescription());
        reparation.setConsigneClient(reparationDto.getConsigneClient());
        reparation.setCout(reparationDto.getCout());
        reparation.setStatut(reparationDto.getStatut());
        reparation.setVehicule(vehicule);
        reparation.setMecanicienUtilisateur(mecanicien);
        reparation.setGarage(garage);
        return reparation;
    }
}
