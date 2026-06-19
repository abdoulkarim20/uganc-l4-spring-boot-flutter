package gn.uganc.gestiongarage.business.reparation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReparationRepository extends JpaRepository<Reparation, Long> {

    List<Reparation> findByVehiculeProprietaireId(Long proprietaireId);

    List<Reparation> findByMecanicienUtilisateurId(Long mecanicienId);

    List<Reparation> findByGarageId(Long garageId);

    boolean existsByMecanicienUtilisateurId(Long mecanicienId);
}
