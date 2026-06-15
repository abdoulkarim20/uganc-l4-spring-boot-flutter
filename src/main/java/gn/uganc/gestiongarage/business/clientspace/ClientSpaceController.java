package gn.uganc.gestiongarage.business.clientspace;

import gn.uganc.gestiongarage.business.client.Client;
import gn.uganc.gestiongarage.business.client.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReparationRepository reparationRepository;

    public ClientSpaceController(UtilisateurRepository utilisateurRepository, ClientRepository clientRepository,
                                 VehiculeRepository vehiculeRepository, ReparationRepository reparationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reparationRepository = reparationRepository;
    }

    @GetMapping("/dashboard")
    public ClientDashboardDto dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Client client = clientRepository.findByTelephone(utilisateur.getTelephone())
                .orElseThrow(() -> new RuntimeException("Aucune fiche client liée à ce compte"));

        List<Vehicule> vehicules = vehiculeRepository.findByClientId(client.getId());
        List<Reparation> reparations = reparationRepository.findByVehiculeClientId(client.getId());
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
        String mecanicien = reparation.getMecanicien() == null ? "-" : "%s %s".formatted(
                valueOrEmpty(reparation.getMecanicien().getPrenom()),
                valueOrEmpty(reparation.getMecanicien().getNom())
        ).trim();
        return new ClientRepairDto(reparation.getId(), reparation.getDateReparation(), reparation.getDescription(),
                reparation.getCout(), reparation.getStatut(), vehicule.getId(), vehiculeLabel, mecanicien);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
