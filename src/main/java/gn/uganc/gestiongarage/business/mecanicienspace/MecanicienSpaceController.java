package gn.uganc.gestiongarage.business.mecanicienspace;

import gn.uganc.gestiongarage.business.mecanicienspace.dtos.MecanicienDashboardDto;
import gn.uganc.gestiongarage.business.mecanicienspace.dtos.MecanicienProfileDto;
import gn.uganc.gestiongarage.business.mecanicienspace.dtos.MecanicienRepairDto;
import gn.uganc.gestiongarage.business.reparation.Reparation;
import gn.uganc.gestiongarage.business.reparation.ReparationRepository;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/mecanicien-space")
public class MecanicienSpaceController {

    private final UtilisateurRepository utilisateurRepository;
    private final ReparationRepository reparationRepository;

    public MecanicienSpaceController(UtilisateurRepository utilisateurRepository,
                                     ReparationRepository reparationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.reparationRepository = reparationRepository;
    }

    @GetMapping("/dashboard")
    public MecanicienDashboardDto dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur mecanicien = utilisateurRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        List<Reparation> reparations = reparationRepository.findByMecanicienUtilisateurId(mecanicien.getId());
        long enCours = reparations.stream().filter(this::isActive).count();
        long terminees = reparations.stream().filter(this::isDone).count();
        BigDecimal chiffreAffecte = reparations.stream()
                .map(Reparation::getCout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MecanicienDashboardDto(
                new MecanicienProfileDto(mecanicien.getId(), mecanicien.getNom(), mecanicien.getPrenom(),
                        mecanicien.getTelephone(), mecanicien.getSpecialite()),
                reparations.stream().map(this::toRepairDto).toList(),
                enCours,
                terminees,
                chiffreAffecte
        );
    }

    private MecanicienRepairDto toRepairDto(Reparation reparation) {
        Vehicule vehicule = reparation.getVehicule();
        Utilisateur client = vehicule.getProprietaire();
        String vehiculeLabel = "%s %s %s".formatted(
                valueOrEmpty(vehicule.getImmatriculation()),
                valueOrEmpty(vehicule.getMarque()),
                valueOrEmpty(vehicule.getModele())
        ).trim();
        String clientName = client == null ? "-" : "%s %s".formatted(
                valueOrEmpty(client.getPrenom()),
                valueOrEmpty(client.getNom())
        ).trim();
        String clientPhone = client == null ? "-" : client.getTelephone();
        return new MecanicienRepairDto(reparation.getId(), reparation.getDateReparation(), reparation.getDescription(),
                reparation.getCout(), reparation.getStatut(), vehicule.getId(), vehiculeLabel, clientName,
                clientPhone);
    }

    private boolean isActive(Reparation reparation) {
        String statut = reparation.getStatut() == null ? "" : reparation.getStatut().toLowerCase();
        return statut.contains("cours") || statut.contains("plan");
    }

    private boolean isDone(Reparation reparation) {
        String statut = reparation.getStatut() == null ? "" : reparation.getStatut().toLowerCase();
        return statut.contains("term");
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
