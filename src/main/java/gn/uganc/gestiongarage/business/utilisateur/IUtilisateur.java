package gn.uganc.gestiongarage.business.utilisateur;

import gn.uganc.gestiongarage.business.utilisateur.dtos.UtilisateurDto;

import java.util.List;

public interface IUtilisateur {

    UtilisateurDto create(UtilisateurDto utilisateurDto);

    List<UtilisateurDto> getAll();

    UtilisateurDto getById(Long id);

    UtilisateurDto update(Long id, UtilisateurDto utilisateurDto);

    void delete(Long id);
}
