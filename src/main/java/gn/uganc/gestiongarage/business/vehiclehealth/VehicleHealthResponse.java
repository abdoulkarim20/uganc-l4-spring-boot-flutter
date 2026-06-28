package gn.uganc.gestiongarage.business.vehiclehealth;

import java.util.List;

public record VehicleHealthResponse(
        String immatriculation,
        String vehicule,
        String proprietaire,
        String niveau,
        String message,
        int totalReparations,
        int reparationsEnCours,
        int reparationsTerminees,
        List<VehicleHealthRepairDto> dernieresReparations
) {
}
