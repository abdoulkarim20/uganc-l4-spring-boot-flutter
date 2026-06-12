package gn.uganc.gestiongarage.business.vehicule.mappers;

import gn.uganc.gestiongarage.business.client.Client;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;
import org.springframework.stereotype.Component;

@Component
public class VehiculeMapper {

    public VehiculeDto toDto(Vehicule vehicule) {
        VehiculeDto vehiculeDto = new VehiculeDto();
        vehiculeDto.setId(vehicule.getId());
        vehiculeDto.setImmatriculation(vehicule.getImmatriculation());
        vehiculeDto.setMarque(vehicule.getMarque());
        vehiculeDto.setModele(vehicule.getModele());
        vehiculeDto.setAnnee(vehicule.getAnnee());
        vehiculeDto.setClientId(vehicule.getClient() != null ? vehicule.getClient().getId() : null);
        return vehiculeDto;
    }

    public Vehicule toEntity(VehiculeDto vehiculeDto, Client client) {
        Vehicule vehicule = new Vehicule();
        vehicule.setId(vehiculeDto.getId());
        vehicule.setImmatriculation(vehiculeDto.getImmatriculation());
        vehicule.setMarque(vehiculeDto.getMarque());
        vehicule.setModele(vehiculeDto.getModele());
        vehicule.setAnnee(vehiculeDto.getAnnee());
        vehicule.setClient(client);
        return vehicule;
    }
}
