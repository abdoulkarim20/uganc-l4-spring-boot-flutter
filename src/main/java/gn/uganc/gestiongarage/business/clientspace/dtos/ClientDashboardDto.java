package gn.uganc.gestiongarage.business.clientspace.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ClientDashboardDto(
        ClientProfileDto client,
        List<ClientVehicleDto> vehicules,
        List<ClientRepairDto> reparations,
        BigDecimal totalDepenses,
        long reparationsEnCours
) {
}
