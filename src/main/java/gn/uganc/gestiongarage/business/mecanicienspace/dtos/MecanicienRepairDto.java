package gn.uganc.gestiongarage.business.mecanicienspace.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MecanicienRepairDto(
        Long id,
        LocalDate dateReparation,
        String description,
        BigDecimal cout,
        String statut,
        Long vehiculeId,
        String vehicule,
        String client,
        String telephoneClient
) {
}
