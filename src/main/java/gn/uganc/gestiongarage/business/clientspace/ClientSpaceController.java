package gn.uganc.gestiongarage.business.clientspace;

import gn.uganc.gestiongarage.business.clientspace.dtos.ClientDashboardDto;
import gn.uganc.gestiongarage.business.clientspace.dtos.ClientProfileDto;
import gn.uganc.gestiongarage.business.clientspace.dtos.ClientRepairDto;
import gn.uganc.gestiongarage.business.clientspace.dtos.ClientVehicleDto;
import gn.uganc.gestiongarage.business.reparation.Reparation;
import gn.uganc.gestiongarage.business.reparation.ReparationRepository;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.business.vehicule.VehiculeRepository;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/client-space")
public class ClientSpaceController {

    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReparationRepository reparationRepository;

    public ClientSpaceController(UtilisateurRepository utilisateurRepository, VehiculeRepository vehiculeRepository,
                                 ReparationRepository reparationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reparationRepository = reparationRepository;
    }

    @GetMapping("/dashboard")
    public ClientDashboardDto dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur client = utilisateurRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        List<Vehicule> vehicules = vehiculeRepository.findByProprietaireId(client.getId());
        List<Reparation> reparations = reparationRepository.findByVehiculeProprietaireId(client.getId());
        BigDecimal totalDepenses = reparations.stream()
                .map(Reparation::getCout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long reparationsEnCours = reparations.stream()
                .filter(reparation -> {
                    String statut = reparation.getStatut() == null ? "" : reparation.getStatut().toLowerCase();
                    return statut.contains("cours") || statut.contains("plan");
                })
                .count();

        return new ClientDashboardDto(
                new ClientProfileDto(client.getId(), client.getNom(), client.getPrenom(), client.getTelephone(), client.getAdresse()),
                vehicules.stream().map(this::toVehicleDto).toList(),
                reparations.stream().map(this::toRepairDto).toList(),
                totalDepenses,
                reparationsEnCours
        );
    }

    private ClientVehicleDto toVehicleDto(Vehicule vehicule) {
        return new ClientVehicleDto(vehicule.getId(), vehicule.getImmatriculation(), vehicule.getMarque(),
                vehicule.getModele(), vehicule.getAnnee());
    }

    private ClientRepairDto toRepairDto(Reparation reparation) {
        Vehicule vehicule = reparation.getVehicule();
        String vehiculeLabel = "%s %s %s".formatted(
                valueOrEmpty(vehicule.getImmatriculation()),
                valueOrEmpty(vehicule.getMarque()),
                valueOrEmpty(vehicule.getModele())
        ).trim();
        Utilisateur mecanicien = reparation.getMecanicienUtilisateur();
        String mecanicienName = mecanicien == null ? "-" : "%s %s".formatted(
                valueOrEmpty(mecanicien.getPrenom()),
                valueOrEmpty(mecanicien.getNom())
        ).trim();
        return new ClientRepairDto(reparation.getId(), reparation.getDateReparation(), reparation.getDescription(),
                reparation.getCout(), reparation.getStatut(), vehicule.getId(), vehiculeLabel, mecanicienName);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
