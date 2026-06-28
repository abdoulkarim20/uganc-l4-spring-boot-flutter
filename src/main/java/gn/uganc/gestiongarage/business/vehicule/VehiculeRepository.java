package gn.uganc.gestiongarage.business.vehicule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByProprietaireId(Long proprietaireId);

    List<Vehicule> findDistinctByReparationsGarageId(Long garageId);

    Optional<Vehicule> findByImmatriculationIgnoreCaseAndCodeAcces(String immatriculation, String codeAcces);

    Optional<Vehicule> findByImmatriculationIgnoreCase(String immatriculation);

    boolean existsByProprietaireId(Long proprietaireId);

    boolean existsByCodeAcces(String codeAcces);
}
