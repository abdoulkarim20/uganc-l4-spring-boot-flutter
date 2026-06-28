package gn.uganc.gestiongarage.business.registration.dtos;

public record ClientRegistrationResponse(
        Long clientId,
        Long vehiculeId,
        String username,
        String message
) {
}
