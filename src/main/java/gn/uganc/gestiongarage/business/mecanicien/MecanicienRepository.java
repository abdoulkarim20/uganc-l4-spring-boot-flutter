package gn.uganc.gestiongarage.business.mecanicien;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MecanicienRepository extends JpaRepository<Mecanicien, Long> {

    Optional<Mecanicien> findByTelephone(String telephone);
}
