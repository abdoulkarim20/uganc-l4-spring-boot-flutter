package gn.uganc.gestiongarage.business.mecanicienspace.dtos;

public record MecanicienProfileDto(
        Long id,
        String nom,
        String prenom,
        String telephone,
        String specialite
) {
}
