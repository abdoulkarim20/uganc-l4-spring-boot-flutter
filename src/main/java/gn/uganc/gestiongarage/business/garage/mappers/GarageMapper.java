package gn.uganc.gestiongarage.business.garage.mappers;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;
import org.springframework.stereotype.Component;

@Component
public class GarageMapper {

    public GarageDto toDto(Garage garage) {
        GarageDto dto = new GarageDto();
        dto.setId(garage.getId());
        dto.setNom(garage.getNom());
        dto.setTelephone(garage.getTelephone());
        dto.setEmail(garage.getEmail());
        dto.setAdresse(garage.getAdresse());
        dto.setVille(garage.getVille());
        dto.setQuartier(garage.getQuartier());
        dto.setPays(garage.getPays());
        dto.setLatitude(garage.getLatitude());
        dto.setLongitude(garage.getLongitude());
        dto.setDescription(garage.getDescription());
        dto.setNomResponsable(garage.getNomResponsable());
        dto.setTelephoneResponsable(garage.getTelephoneResponsable());
        dto.setStatut(garage.getStatut());
        dto.setDateCreation(garage.getDateCreation());
        return dto;
    }

    public Garage toEntity(GarageDto dto) {
        Garage garage = new Garage();
        garage.setId(dto.getId());
        garage.setNom(dto.getNom());
        garage.setTelephone(dto.getTelephone());
        garage.setEmail(dto.getEmail());
        garage.setAdresse(dto.getAdresse());
        garage.setVille(dto.getVille());
        garage.setQuartier(dto.getQuartier());
        garage.setPays(dto.getPays());
        garage.setLatitude(dto.getLatitude());
        garage.setLongitude(dto.getLongitude());
        garage.setDescription(dto.getDescription());
        garage.setNomResponsable(dto.getNomResponsable());
        garage.setTelephoneResponsable(dto.getTelephoneResponsable());
        garage.setStatut(dto.getStatut());
        garage.setDateCreation(dto.getDateCreation());
        return garage;
    }
}
