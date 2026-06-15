package gn.uganc.gestiongarage.business.clientspace.dtos;

public record ClientVehicleDto(
        Long id,
        String immatriculation,
        String marque,
        String modele,
        Integer annee
) {
}
