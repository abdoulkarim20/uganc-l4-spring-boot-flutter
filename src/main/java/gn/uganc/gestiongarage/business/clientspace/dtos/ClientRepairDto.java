package gn.uganc.gestiongarage.business.clientspace.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientRepairDto(
        Long id,
        LocalDate dateReparation,
        String description,
        BigDecimal cout,
        String statut,
        Long vehiculeId,
        String vehicule,
        String mecanicien
) {
}
