package gn.uganc.gestiongarage.business.garage;

import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;
import gn.uganc.gestiongarage.business.garage.mappers.GarageMapper;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class GarageImpl implements IGarage {

    private final GarageRepository garageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GarageMapper garageMapper;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public GarageImpl(GarageRepository garageRepository, UtilisateurRepository utilisateurRepository,
                      GarageMapper garageMapper, CurrentUserService currentUserService, PasswordEncoder passwordEncoder) {
        this.garageRepository = garageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.garageMapper = garageMapper;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public GarageDto create(GarageDto garageDto) {
        validateGarage(garageDto, null);
        Garage garage = garageMapper.toEntity(garageDto);
        if (garage.getStatut() == null) {
            garage.setStatut(StatutGarage.EN_ATTENTE);
        }
        Garage saved = garageRepository.save(garage);
        createAdminGarageAccountIfActive(saved);
        return garageMapper.toDto(saved);
    }

    @Override
    public List<GarageDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return List.of(garageMapper.toDto(findGarage(currentUserService.getCurrentGarageId())));
        }
        return garageRepository.findAll().stream()
                .map(garageMapper::toDto)
                .toList();
    }

    @Override
    public GarageDto getById(Long id) {
        return garageMapper.toDto(findGarage(id));
    }

    @Override
    public GarageDto update(Long id, GarageDto garageDto) {
        Garage garage = findGarage(id);
        StatutGarage oldStatus = garage.getStatut();
        validateGarage(garageDto, id);
        garage.setNom(garageDto.getNom());
        garage.setTelephone(garageDto.getTelephone());
        garage.setEmail(garageDto.getEmail());
        garage.setAdresse(garageDto.getAdresse());
        garage.setVille(garageDto.getVille());
        garage.setQuartier(garageDto.getQuartier());
        garage.setPays(garageDto.getPays());
        garage.setLatitude(garageDto.getLatitude());
        garage.setLongitude(garageDto.getLongitude());
        garage.setDescription(garageDto.getDescription());
        garage.setNomResponsable(garageDto.getNomResponsable());
        garage.setTelephoneResponsable(garageDto.getTelephoneResponsable());
        garage.setStatut(garageDto.getStatut() == null ? garage.getStatut() : garageDto.getStatut());
        Garage saved = garageRepository.save(garage);
        if (oldStatus != StatutGarage.ACTIF) {
            createAdminGarageAccountIfActive(saved);
        }
        return garageMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Garage garage = findGarage(id);
        if (utilisateurRepository.existsByGarageId(id)) {
            throw new BusinessException("Impossible de supprimer un garage qui contient des utilisateurs.");
        }
        garageRepository.delete(garage);
    }

    private Garage findGarage(Long id) {
        Garage garage = garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", id));
        if (currentUserService.isGarageAdmin() && !garage.getId().equals(currentUserService.getCurrentGarageId())) {
            throw new ResourceNotFoundException("Garage", id);
        }
        return garage;
    }

    private void validateGarage(GarageDto garageDto, Long currentId) {
        if (!StringUtils.hasText(garageDto.getNom())) {
            throw new BusinessException("Le nom du garage est obligatoire.");
        }
        if (!StringUtils.hasText(garageDto.getTelephone())) {
            throw new BusinessException("Le téléphone du garage est obligatoire.");
        }
        boolean phoneExists = currentId == null
                ? garageRepository.existsByTelephone(garageDto.getTelephone())
                : garageRepository.existsByTelephoneAndIdNot(garageDto.getTelephone(), currentId);
        if (phoneExists) {
            throw new BusinessException("Ce numéro de téléphone est déjà utilisé par un garage.");
        }
    }

    private void createAdminGarageAccountIfActive(Garage garage) {
        if (garage.getStatut() != StatutGarage.ACTIF) {
            return;
        }
        if (utilisateurRepository.existsByRoleAndGarageId(RoleUser.ADMIN_GARAGE, garage.getId())) {
            return;
        }
        String managerPhone = StringUtils.hasText(garage.getTelephoneResponsable())
                ? garage.getTelephoneResponsable()
                : garage.getTelephone();
        if (utilisateurRepository.existsByTelephone(managerPhone) || utilisateurRepository.existsByUsername(managerPhone)) {
            throw new BusinessException("Le téléphone du responsable est déjà utilisé par un utilisateur.");
        }

        Utilisateur adminGarage = new Utilisateur();
        adminGarage.setNom(resolveManagerName(garage));
        adminGarage.setPrenom("Responsable");
        adminGarage.setTelephone(managerPhone);
        adminGarage.setUsername(managerPhone);
        adminGarage.setPassword(passwordEncoder.encode(managerPhone));
        adminGarage.setMustChangePassword(true);
        adminGarage.setRole(RoleUser.ADMIN_GARAGE);
        adminGarage.setEmail(garage.getEmail());
        adminGarage.setAdresse(garage.getAdresse());
        adminGarage.setGarage(garage);
        utilisateurRepository.save(adminGarage);
    }

    private String resolveManagerName(Garage garage) {
        if (StringUtils.hasText(garage.getNomResponsable())) {
            return garage.getNomResponsable();
        }
        return garage.getNom();
    }
}
