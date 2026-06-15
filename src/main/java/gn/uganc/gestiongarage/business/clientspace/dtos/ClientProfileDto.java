package gn.uganc.gestiongarage.business.clientspace.dtos;

public record ClientProfileDto(
        Long id,
        String nom,
        String prenom,
        String telephone,
        String adresse
) {
}
