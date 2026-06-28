package gn.uganc.gestiongarage.business.client;

import gn.uganc.gestiongarage.business.client.dtos.ClientDto;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.VehiculeRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClientImpl implements IClient {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final VehiculeRepository vehiculeRepository;
    private final CurrentUserService currentUserService;

    public ClientImpl(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,
                      VehiculeRepository vehiculeRepository, CurrentUserService currentUserService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.vehiculeRepository = vehiculeRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public ClientDto create(ClientDto clientDto) {
        validateClient(clientDto, null);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(clientDto.getNom());
        utilisateur.setPrenom(clientDto.getPrenom());
        utilisateur.setTelephone(clientDto.getTelephone());
        utilisateur.setUsername(clientDto.getTelephone());
        utilisateur.setAdresse(clientDto.getAdresse());
        utilisateur.setRole(RoleUser.CLIENT);
        utilisateur.setPassword(passwordEncoder.encode(StringUtils.hasText(clientDto.getPassword()) ? clientDto.getPassword() : clientDto.getTelephone()));
        utilisateur.setMustChangePassword(true);
        return toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public List<ClientDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return utilisateurRepository.findClientsByGarageRepairs(currentUserService.getCurrentGarageId(), RoleUser.CLIENT)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        return utilisateurRepository.findByRole(RoleUser.CLIENT).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ClientDto getById(Long id) {
        return toDto(findClientUser(id));
    }

    @Override
    public ClientDto getByTelephone(String telephone) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
        if (utilisateur.getRole() != RoleUser.CLIENT) {
            throw new ResourceNotFoundException("Client introuvable");
        }
        return toDto(utilisateur);
    }

    @Override
    public ClientDto update(Long id, ClientDto clientDto) {
        Utilisateur utilisateur = findClientUser(id);
        validateClient(clientDto, id);
        String oldTelephone = utilisateur.getTelephone();
        utilisateur.setNom(clientDto.getNom());
        utilisateur.setPrenom(clientDto.getPrenom());
        utilisateur.setTelephone(clientDto.getTelephone());
        utilisateur.setAdresse(clientDto.getAdresse());
        if (oldTelephone != null && oldTelephone.equals(utilisateur.getUsername())) {
            utilisateur.setUsername(clientDto.getTelephone());
        }
        return toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = findClientUser(id);
        if (vehiculeRepository.existsByProprietaireId(id)) {
            throw new BusinessException("Impossible de supprimer ce client car il possede des vehicules.");
        }
        utilisateurRepository.delete(utilisateur);
    }

    private Utilisateur findClientUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        if (utilisateur.getRole() != RoleUser.CLIENT) {
            throw new ResourceNotFoundException("Client", id);
        }
        if (currentUserService.isGarageAdmin() && !isVisibleForCurrentGarage(utilisateur.getId())) {
            throw new ResourceNotFoundException("Client", id);
        }
        return utilisateur;
    }

    private boolean isVisibleForCurrentGarage(Long clientId) {
        return utilisateurRepository.findClientsByGarageRepairs(currentUserService.getCurrentGarageId(), RoleUser.CLIENT)
                .stream()
                .anyMatch(client -> client.getId().equals(clientId));
    }

    private void validateClient(ClientDto clientDto, Long currentId) {
        if (!StringUtils.hasText(clientDto.getNom())) {
            throw new BusinessException("Le nom du client est obligatoire.");
        }
        if (!StringUtils.hasText(clientDto.getPrenom())) {
            throw new BusinessException("Le prenom du client est obligatoire.");
        }
        if (!StringUtils.hasText(clientDto.getTelephone())) {
            throw new BusinessException("Le telephone du client est obligatoire.");
        }
        boolean telephoneExists = currentId == null
                ? utilisateurRepository.existsByTelephone(clientDto.getTelephone())
                : utilisateurRepository.existsByTelephoneAndIdNot(clientDto.getTelephone(), currentId);
        if (telephoneExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise.");
        }
        boolean usernameExists = currentId == null
                ? utilisateurRepository.existsByUsername(clientDto.getTelephone())
                : utilisateurRepository.existsByUsernameAndIdNot(clientDto.getTelephone(), currentId);
        if (usernameExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise comme identifiant.");
        }
    }

    private ClientDto toDto(Utilisateur utilisateur) {
        ClientDto dto = new ClientDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setAdresse(utilisateur.getAdresse());
        return dto;
    }
}
