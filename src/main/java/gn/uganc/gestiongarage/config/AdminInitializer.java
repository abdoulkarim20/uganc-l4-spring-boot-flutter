package gn.uganc.gestiongarage.config;

import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UtilisateurRepository utilisateurRepository,
                            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifie si un admin existe déjà
        boolean adminExiste = utilisateurRepository.existsByRole(RoleUser.ADMIN);
        if (!adminExiste) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("DIALLO");
            admin.setPrenom("Abdoul Karim");
            admin.setTelephone("610781799");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(RoleUser.ADMIN);
            admin.setMustChangePassword(false);
            utilisateurRepository.save(admin);
            System.out.println("=== ADMIN PAR DEFAUT CREE ===");
        }
    }
}
