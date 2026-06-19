package gn.uganc.gestiongarage.security;

import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UtilisateurRepository utilisateurRepository;

    public CurrentUserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Authentification requise.");
        }
        return utilisateurRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }

    public boolean isGarageAdmin() {
        return getCurrentUser().getRole() == RoleUser.ADMIN_GARAGE;
    }

    public Long getCurrentGarageId() {
        Utilisateur utilisateur = getCurrentUser();
        if (utilisateur.getGarage() == null) {
            throw new BusinessException("Aucun garage n'est rattache a ce compte.");
        }
        return utilisateur.getGarage().getId();
    }
}
