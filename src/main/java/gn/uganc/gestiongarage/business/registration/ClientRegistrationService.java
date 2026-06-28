package gn.uganc.gestiongarage.business.registration;

import gn.uganc.gestiongarage.business.registration.dtos.ClientRegistrationRequest;
import gn.uganc.gestiongarage.business.registration.dtos.ClientRegistrationResponse;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClientRegistrationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientRegistrationService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        validate(request);

        Utilisateur client = new Utilisateur();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setTelephone(request.getTelephone());
        client.setUsername(request.getTelephone());
        client.setAdresse(request.getAdresse());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setRole(RoleUser.CLIENT);
        client.setMustChangePassword(false);
        Utilisateur savedClient = utilisateurRepository.save(client);

        return new ClientRegistrationResponse(
                savedClient.getId(),
                null,
                savedClient.getUsername(),
                "Votre espace client a bien ete cree."
        );
    }

    private void validate(ClientRegistrationRequest request) {
        if (!StringUtils.hasText(request.getNom())) {
            throw new BusinessException("Le nom est obligatoire.");
        }
        if (!StringUtils.hasText(request.getPrenom())) {
            throw new BusinessException("Le prenom est obligatoire.");
        }
        if (!StringUtils.hasText(request.getTelephone())) {
            throw new BusinessException("Le telephone est obligatoire.");
        }
        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 6) {
            throw new BusinessException("Le mot de passe doit contenir au moins 6 caracteres.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("La confirmation du mot de passe ne correspond pas.");
        }
        if (utilisateurRepository.existsByTelephone(request.getTelephone())
                || utilisateurRepository.existsByUsername(request.getTelephone())) {
            throw new BusinessException("Ce telephone est deja utilise sur la plateforme.");
        }
    }
}
