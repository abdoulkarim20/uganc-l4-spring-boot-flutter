package gn.uganc.gestiongarage.business.mecanicienspace.dtos;

import java.math.BigDecimal;
import java.util.List;

public record MecanicienDashboardDto(
        MecanicienProfileDto mecanicien,
        List<MecanicienRepairDto> reparations,
        long reparationsEnCours,
        long reparationsTerminees,
        BigDecimal chiffreAffecte
) {
}
