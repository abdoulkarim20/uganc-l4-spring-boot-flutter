package gn.uganc.gestiongarage.business.vehicule;

import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;
import gn.uganc.gestiongarage.business.vehicule.mappers.VehiculeMapper;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;

@Service
public class VehiculeImpl implements IVehicule {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeMapper vehiculeMapper;
    private final CurrentUserService currentUserService;

    public VehiculeImpl(VehiculeRepository vehiculeRepository, UtilisateurRepository utilisateurRepository,
                        VehiculeMapper vehiculeMapper, CurrentUserService currentUserService) {
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeMapper = vehiculeMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public VehiculeDto create(VehiculeDto vehiculeDto) {
        ensureUniqueImmatriculation(vehiculeDto.getImmatriculation(), null);
        Utilisateur proprietaire = findClientUser(vehiculeDto.getClientId());
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDto, proprietaire);
        ensureAccessCode(vehicule);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public List<VehiculeDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return vehiculeRepository.findDistinctByReparationsGarageId(currentUserService.getCurrentGarageId()).stream()
                    .map(vehiculeMapper::toDto)
                    .toList();
        }
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    public List<VehiculeDto> getByClientId(Long clientId) {
        findClientUser(clientId);
        return vehiculeRepository.findByProprietaireId(clientId).stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    public VehiculeDto getById(Long id) {
        return vehiculeMapper.toDto(findVehicule(id));
    }

    @Override
    public VehiculeDto update(Long id, VehiculeDto vehiculeDto) {
        Vehicule vehicule = findVehicule(id);
        ensureUniqueImmatriculation(vehiculeDto.getImmatriculation(), id);
        Utilisateur proprietaire = findClientUser(vehiculeDto.getClientId());
        vehicule.setImmatriculation(vehiculeDto.getImmatriculation());
        vehicule.setMarque(vehiculeDto.getMarque());
        vehicule.setModele(vehiculeDto.getModele());
        vehicule.setAnnee(vehiculeDto.getAnnee());
        if (StringUtils.hasText(vehiculeDto.getCodeAcces())) {
            vehicule.setCodeAcces(vehiculeDto.getCodeAcces().trim().toUpperCase());
        }
        ensureAccessCode(vehicule);
        vehicule.setProprietaire(proprietaire);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        vehiculeRepository.delete(findVehicule(id));
    }

    private Vehicule findVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        if (currentUserService.isGarageAdmin() && !isVisibleForCurrentGarage(vehicule.getId())) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        return vehicule;
    }

    private boolean isVisibleForCurrentGarage(Long vehiculeId) {
        return vehiculeRepository.findDistinctByReparationsGarageId(currentUserService.getCurrentGarageId())
                .stream()
                .anyMatch(vehicule -> vehicule.getId().equals(vehiculeId));
    }

    private Utilisateur findClientUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        if (utilisateur.getRole() != RoleUser.CLIENT) {
            throw new BusinessException("Le proprietaire du vehicule doit etre un client.");
        }
        return utilisateur;
    }

    private void ensureUniqueImmatriculation(String immatriculation, Long currentVehiculeId) {
        if (!StringUtils.hasText(immatriculation)) {
            return;
        }
        vehiculeRepository.findByImmatriculationIgnoreCase(immatriculation.trim())
                .filter(vehicule -> currentVehiculeId == null || !vehicule.getId().equals(currentVehiculeId))
                .ifPresent(vehicule -> {
                    throw new BusinessException("Ce vehicule est deja enregistre.");
                });
    }

    private void ensureAccessCode(Vehicule vehicule) {
        if (StringUtils.hasText(vehicule.getCodeAcces())) {
            vehicule.setCodeAcces(vehicule.getCodeAcces().trim().toUpperCase());
            return;
        }
        String code;
        do {
            code = generateAccessCode();
        } while (vehiculeRepository.existsByCodeAcces(code));
        vehicule.setCodeAcces(code);
    }

    private String generateAccessCode() {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return builder.toString();
    }
}
