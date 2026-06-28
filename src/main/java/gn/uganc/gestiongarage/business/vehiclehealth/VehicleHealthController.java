package gn.uganc.gestiongarage.business.vehiclehealth;

import gn.uganc.gestiongarage.business.reparation.Reparation;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.business.vehicule.VehiculeRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/public/vehicle-health")
public class VehicleHealthController {

    private final VehiculeRepository vehiculeRepository;

    public VehicleHealthController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @PostMapping
    @Transactional(readOnly = true)
    public VehicleHealthResponse check(@RequestBody VehicleHealthRequest request) {
        if (!StringUtils.hasText(request.immatriculation()) || !StringUtils.hasText(request.code())) {
            throw new BusinessException("Immatriculation et code d'accès obligatoires.");
        }
        Vehicule vehicule = vehiculeRepository
                .findByImmatriculationIgnoreCaseAndCodeAcces(
                        request.immatriculation().trim(),
                        request.code().trim().toUpperCase()
                )
                .orElseThrow(() -> new BusinessException("Aucun véhicule trouvé avec ces informations."));

        List<Reparation> reparations = vehicule.getReparations().stream()
                .sorted(Comparator.comparing(Reparation::getDateReparation).reversed())
                .toList();
        int enCours = (int) reparations.stream().filter(this::isOpenRepair).count();
        int terminees = (int) reparations.stream().filter(this::isDoneRepair).count();
        String niveau = resolveHealthLevel(reparations.size(), enCours);

        return new VehicleHealthResponse(
                vehicule.getImmatriculation(),
                "%s %s".formatted(vehicule.getMarque(), vehicule.getModele()).trim(),
                vehicule.getProprietaire() == null ? "-" : "%s %s".formatted(
                        vehicule.getProprietaire().getPrenom(),
                        vehicule.getProprietaire().getNom()
                ).trim(),
                niveau,
                resolveHealthMessage(niveau, enCours),
                reparations.size(),
                enCours,
                terminees,
                reparations.stream().limit(5).map(this::toRepairDto).toList()
        );
    }

    private VehicleHealthRepairDto toRepairDto(Reparation reparation) {
        return new VehicleHealthRepairDto(
                reparation.getDateReparation(),
                reparation.getDescription(),
                reparation.getStatut(),
                reparation.getConsigneClient(),
                resolveGenericAdvice(reparation),
                reparation.getCout(),
                reparation.getGarage() == null ? "-" : reparation.getGarage().getNom(),
                reparation.getMecanicienUtilisateur() == null ? "-" : "%s %s".formatted(
                        reparation.getMecanicienUtilisateur().getPrenom(),
                        reparation.getMecanicienUtilisateur().getNom()
                ).trim()
        );
    }

    private boolean isOpenRepair(Reparation reparation) {
        String statut = normalizeStatus(reparation);
        return statut.contains("cours") || statut.contains("plan");
    }

    private boolean isDoneRepair(Reparation reparation) {
        return normalizeStatus(reparation).contains("term");
    }

    private String normalizeStatus(Reparation reparation) {
        return reparation.getStatut() == null ? "" : reparation.getStatut().toLowerCase();
    }

    private String resolveGenericAdvice(Reparation reparation) {
        String statut = normalizeStatus(reparation);
        if (statut.contains("plan")) {
            return "Une intervention est programmée. Respectez le rendez-vous et contactez le garage si le symptôme s'aggrave.";
        }
        if (statut.contains("cours")) {
            return "Le véhicule est actuellement suivi par le garage. Attendez la fin du diagnostic avant de reprendre la route.";
        }
        if (statut.contains("term")) {
            return "L'intervention est terminée. Restez attentif aux signes inhabituels et suivez les consignes du garage.";
        }
        if (statut.contains("annul")) {
            return "Cette intervention n'a pas été réalisée. Contactez le garage si le problème est toujours présent.";
        }
        return "Surveillez le comportement du véhicule et contactez le garage en cas de bruit, voyant ou sensation inhabituelle.";
    }

    private String resolveHealthLevel(int total, int enCours) {
        if (enCours > 0) {
            return "À surveiller";
        }
        if (total == 0) {
            return "Aucun historique";
        }
        return "Stable";
    }

    private String resolveHealthMessage(String niveau, int enCours) {
        if ("À surveiller".equals(niveau)) {
            return "%d intervention(s) en cours ou planifiée(s). Suivez les recommandations du garage.".formatted(enCours);
        }
        if ("Aucun historique".equals(niveau)) {
            return "Aucune réparation enregistrée pour ce véhicule dans GARAGIX.";
        }
        return "Aucune intervention ouverte. Le dernier historique est disponible ci-dessous.";
    }
}
