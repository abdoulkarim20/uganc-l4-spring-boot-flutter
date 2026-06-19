package gn.uganc.gestiongarage.business.mecanicien;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.garage.GarageRepository;
import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;
import gn.uganc.gestiongarage.business.reparation.ReparationRepository;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MecanicienImpl implements IMecanicien {

    private final UtilisateurRepository utilisateurRepository;
    private final GarageRepository garageRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReparationRepository reparationRepository;
    private final CurrentUserService currentUserService;

    public MecanicienImpl(UtilisateurRepository utilisateurRepository, GarageRepository garageRepository,
                          PasswordEncoder passwordEncoder, ReparationRepository reparationRepository,
                          CurrentUserService currentUserService) {
        this.utilisateurRepository = utilisateurRepository;
        this.garageRepository = garageRepository;
        this.passwordEncoder = passwordEncoder;
        this.reparationRepository = reparationRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public MecanicienDto create(MecanicienDto mecanicienDto) {
        if (currentUserService.isGarageAdmin()) {
            mecanicienDto.setGarageId(currentUserService.getCurrentGarageId());
        }
        validateMecanicien(mecanicienDto, null);
        Garage garage = findGarage(mecanicienDto.getGarageId());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(mecanicienDto.getNom());
        utilisateur.setPrenom(mecanicienDto.getPrenom());
        utilisateur.setTelephone(mecanicienDto.getTelephone());
        utilisateur.setUsername(mecanicienDto.getTelephone());
        utilisateur.setSpecialite(mecanicienDto.getSpecialite());
        utilisateur.setGarage(garage);
        utilisateur.setRole(RoleUser.MECANICIEN);
        utilisateur.setPassword(passwordEncoder.encode(StringUtils.hasText(mecanicienDto.getPassword())
                ? mecanicienDto.getPassword()
                : mecanicienDto.getTelephone()));
        utilisateur.setMustChangePassword(true);

        return toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public List<MecanicienDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return utilisateurRepository.findByRoleAndGarageId(RoleUser.MECANICIEN, currentUserService.getCurrentGarageId())
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return utilisateurRepository.findByRole(RoleUser.MECANICIEN).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MecanicienDto getById(Long id) {
        return toDto(findMecanicienUser(id));
    }

    @Override
    public MecanicienDto update(Long id, MecanicienDto mecanicienDto) {
        Utilisateur utilisateur = findMecanicienUser(id);
        if (currentUserService.isGarageAdmin()) {
            mecanicienDto.setGarageId(currentUserService.getCurrentGarageId());
        }
        validateMecanicien(mecanicienDto, id);
        Garage garage = findGarage(mecanicienDto.getGarageId());
        String oldTelephone = utilisateur.getTelephone();

        utilisateur.setNom(mecanicienDto.getNom());
        utilisateur.setPrenom(mecanicienDto.getPrenom());
        utilisateur.setTelephone(mecanicienDto.getTelephone());
        utilisateur.setSpecialite(mecanicienDto.getSpecialite());
        utilisateur.setGarage(garage);
        if (oldTelephone != null && oldTelephone.equals(utilisateur.getUsername())) {
            utilisateur.setUsername(mecanicienDto.getTelephone());
        }

        return toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public void delete(Long id) {
        if (reparationRepository.existsByMecanicienUtilisateurId(id)) {
            throw new BusinessException("Impossible de supprimer ce mecanicien car il est affecte a des reparations.");
        }
        utilisateurRepository.delete(findMecanicienUser(id));
    }

    private Utilisateur findMecanicienUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mecanicien", id));
        if (utilisateur.getRole() != RoleUser.MECANICIEN) {
            throw new ResourceNotFoundException("Mecanicien", id);
        }
        if (currentUserService.isGarageAdmin()
                && (utilisateur.getGarage() == null || !utilisateur.getGarage().getId().equals(currentUserService.getCurrentGarageId()))) {
            throw new ResourceNotFoundException("Mecanicien", id);
        }
        return utilisateur;
    }

    private Garage findGarage(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", id));
    }

    private void validateMecanicien(MecanicienDto mecanicienDto, Long currentId) {
        if (!StringUtils.hasText(mecanicienDto.getNom())) {
            throw new BusinessException("Le nom du mecanicien est obligatoire.");
        }
        if (!StringUtils.hasText(mecanicienDto.getPrenom())) {
            throw new BusinessException("Le prenom du mecanicien est obligatoire.");
        }
        if (!StringUtils.hasText(mecanicienDto.getTelephone())) {
            throw new BusinessException("Le telephone du mecanicien est obligatoire.");
        }
        if (!StringUtils.hasText(mecanicienDto.getSpecialite())) {
            throw new BusinessException("La specialite du mecanicien est obligatoire.");
        }
        if (mecanicienDto.getGarageId() == null) {
            throw new BusinessException("Le garage du mecanicien est obligatoire.");
        }
        boolean telephoneExists = currentId == null
                ? utilisateurRepository.existsByTelephone(mecanicienDto.getTelephone())
                : utilisateurRepository.existsByTelephoneAndIdNot(mecanicienDto.getTelephone(), currentId);
        if (telephoneExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise.");
        }
        boolean usernameExists = currentId == null
                ? utilisateurRepository.existsByUsername(mecanicienDto.getTelephone())
                : utilisateurRepository.existsByUsernameAndIdNot(mecanicienDto.getTelephone(), currentId);
        if (usernameExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise comme identifiant.");
        }
    }

    private MecanicienDto toDto(Utilisateur utilisateur) {
        MecanicienDto dto = new MecanicienDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setSpecialite(utilisateur.getSpecialite());
        if (utilisateur.getGarage() != null) {
            dto.setGarageId(utilisateur.getGarage().getId());
            dto.setGarageNom(utilisateur.getGarage().getNom());
        }
        return dto;
    }
}
