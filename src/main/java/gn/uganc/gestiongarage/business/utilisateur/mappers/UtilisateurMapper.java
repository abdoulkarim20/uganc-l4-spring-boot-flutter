package gn.uganc.gestiongarage.business.utilisateur.mappers;

import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.dtos.UtilisateurDto;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public UtilisateurDto toDto(Utilisateur utilisateur) {
        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setNom(utilisateur.getNom());
        utilisateurDto.setPrenom(utilisateur.getPrenom());
        utilisateurDto.setTelephone(utilisateur.getTelephone());
        utilisateurDto.setUsername(utilisateur.getUsername());
        utilisateurDto.setRole(utilisateur.getRole());
        utilisateurDto.setMustChangePassword(utilisateur.isMustChangePassword());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setAdresse(utilisateur.getAdresse());
        utilisateurDto.setSpecialite(utilisateur.getSpecialite());
        utilisateurDto.setDateCreation(utilisateur.getDateCreation());
        if (utilisateur.getGarage() != null) {
            utilisateurDto.setGarageId(utilisateur.getGarage().getId());
            utilisateurDto.setGarageNom(utilisateur.getGarage().getNom());
        }
        return utilisateurDto;
    }

    public Utilisateur toEntity(UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurDto.getId());
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setTelephone(utilisateurDto.getTelephone());
        utilisateur.setUsername(utilisateurDto.getUsername());
        utilisateur.setPassword(utilisateurDto.getPassword());
        utilisateur.setRole(utilisateurDto.getRole());
        utilisateur.setMustChangePassword(utilisateurDto.isMustChangePassword());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setAdresse(utilisateurDto.getAdresse());
        utilisateur.setSpecialite(utilisateurDto.getSpecialite());
        utilisateur.setDateCreation(utilisateurDto.getDateCreation());
        return utilisateur;
    }
}
