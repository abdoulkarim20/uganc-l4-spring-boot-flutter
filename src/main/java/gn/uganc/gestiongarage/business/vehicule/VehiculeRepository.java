package gn.uganc.gestiongarage.business.vehicule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByClientId(Long clientId);
}
