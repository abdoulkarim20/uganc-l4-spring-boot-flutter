package gn.uganc.gestiongarage.business.reparation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReparationRepository extends JpaRepository<Reparation, Long> {

    List<Reparation> findByVehiculeClientId(Long clientId);

    List<Reparation> findByMecanicienId(Long mecanicienId);
}
