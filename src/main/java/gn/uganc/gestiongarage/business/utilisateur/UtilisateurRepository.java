package gn.uganc.gestiongarage.business.utilisateur;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    Optional<Utilisateur> findByTelephone(String telephone);

    List<Utilisateur> findByRole(RoleUser role);

    List<Utilisateur> findByRoleAndGarageId(RoleUser role, Long garageId);

    @Query("""
            select distinct vehicule.proprietaire
            from Reparation reparation
            join reparation.vehicule vehicule
            where reparation.garage.id = :garageId
            and vehicule.proprietaire.role = :role
            """)
    List<Utilisateur> findClientsByGarageRepairs(@Param("garageId") Long garageId, @Param("role") RoleUser role);

    boolean existsByUsername(String username);

    boolean existsByTelephone(String telephone);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByTelephoneAndIdNot(String telephone, Long id);

    boolean existsByGarageId(Long garageId);

    boolean existsByRoleAndGarageId(RoleUser role, Long garageId);

    boolean existsByRole(RoleUser role);
}
