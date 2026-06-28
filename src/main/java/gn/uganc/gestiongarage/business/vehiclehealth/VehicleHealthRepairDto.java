package gn.uganc.gestiongarage.business.vehiclehealth;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VehicleHealthRepairDto(
        LocalDate date,
        String description,
        String statut,
        String consigneClient,
        String conseil,
        BigDecimal cout,
        String garage,
        String mecanicien
) {
}
