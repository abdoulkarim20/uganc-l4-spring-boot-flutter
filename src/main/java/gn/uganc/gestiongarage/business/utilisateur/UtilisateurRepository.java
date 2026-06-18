package gn.uganc.gestiongarage.business.utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    Optional<Utilisateur> findByTelephone(String telephone);

    boolean existsByUsername(String username);

    boolean existsByTelephone(String telephone);
    boolean existsByRole(RoleUser role);
}
