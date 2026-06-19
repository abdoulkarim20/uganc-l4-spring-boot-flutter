package gn.uganc.gestiongarage.business.utilisateur;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.garage.GarageRepository;
import gn.uganc.gestiongarage.business.utilisateur.dtos.UtilisateurDto;
import gn.uganc.gestiongarage.business.utilisateur.mappers.UtilisateurMapper;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UtilisateurImpl implements IUtilisateur {

    private final UtilisateurRepository utilisateurRepository;
    private final GarageRepository garageRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurImpl(UtilisateurRepository utilisateurRepository, GarageRepository garageRepository,
                           UtilisateurMapper utilisateurMapper, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.garageRepository = garageRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UtilisateurDto create(UtilisateurDto utilisateurDto) {
        validateUtilisateur(utilisateurDto, null);
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);
        utilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));
        utilisateur.setMustChangePassword(utilisateur.getRole() != RoleUser.ADMIN);
        utilisateur.setGarage(resolveGarageForRole(utilisateurDto.getRole(), utilisateurDto.getGarageId()));
        return utilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public List<UtilisateurDto> getAll() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .toList();
    }

    @Override
    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = findUtilisateur(id);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto getUserByUsername(String username) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + username));
        utilisateur.setPassword(null);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto update(Long id, UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = findUtilisateur(id);
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setTelephone(utilisateurDto.getTelephone());
        utilisateur.setUsername(utilisateurDto.getUsername());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setAdresse(utilisateurDto.getAdresse());
        utilisateur.setSpecialite(utilisateurDto.getSpecialite());
        if (StringUtils.hasText(utilisateurDto.getPassword())) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));
        }
        utilisateur.setRole(utilisateurDto.getRole());
        validateUtilisateur(utilisateurDto, id);
        utilisateur.setGarage(resolveGarageForRole(utilisateurDto.getRole(), utilisateurDto.getGarageId()));
        return utilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = findUtilisateur(id);
        if ("admin".equals(utilisateur.getUsername())) {
            throw new BusinessException(
                    "Impossible de supprimer l'utilisateur administrateur."
            );
        }
        utilisateurRepository.delete(utilisateur);
    }

    private Utilisateur findUtilisateur(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }

    private void validateUtilisateur(UtilisateurDto utilisateurDto, Long currentId) {
        if (!StringUtils.hasText(utilisateurDto.getNom())) {
            throw new BusinessException("Le nom de l'utilisateur est obligatoire.");
        }
        if (!StringUtils.hasText(utilisateurDto.getPrenom())) {
            throw new BusinessException("Le prenom de l'utilisateur est obligatoire.");
        }
        if (!StringUtils.hasText(utilisateurDto.getTelephone())) {
            throw new BusinessException("Le telephone de l'utilisateur est obligatoire.");
        }
        if (!StringUtils.hasText(utilisateurDto.getUsername())) {
            throw new BusinessException("L'identifiant de l'utilisateur est obligatoire.");
        }
        if (utilisateurDto.getRole() == null) {
            throw new BusinessException("Le profil de l'utilisateur est obligatoire.");
        }
        if (currentId == null && !StringUtils.hasText(utilisateurDto.getPassword())) {
            throw new BusinessException("Le mot de passe est obligatoire.");
        }
        boolean usernameExists = currentId == null
                ? utilisateurRepository.existsByUsername(utilisateurDto.getUsername())
                : utilisateurRepository.existsByUsernameAndIdNot(utilisateurDto.getUsername(), currentId);
        if (usernameExists) {
            throw new BusinessException("Cet identifiant est deja utilise.");
        }
        boolean telephoneExists = currentId == null
                ? utilisateurRepository.existsByTelephone(utilisateurDto.getTelephone())
                : utilisateurRepository.existsByTelephoneAndIdNot(utilisateurDto.getTelephone(), currentId);
        if (telephoneExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise.");
        }
        if (requiresGarage(utilisateurDto.getRole()) && utilisateurDto.getGarageId() == null) {
            throw new BusinessException("Un garage est obligatoire pour ce profil.");
        }
        if (!requiresGarage(utilisateurDto.getRole()) && utilisateurDto.getGarageId() != null) {
            throw new BusinessException("Ce profil ne doit pas etre rattache a un garage.");
        }
    }

    private Garage resolveGarageForRole(RoleUser role, Long garageId) {
        if (!requiresGarage(role)) {
            return null;
        }
        return garageRepository.findById(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", garageId));
    }

    private boolean requiresGarage(RoleUser role) {
        return role == RoleUser.ADMIN_GARAGE || role == RoleUser.MECANICIEN;
    }
}
