package gn.uganc.gestiongarage.security;

import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String username;
    private final String password;

    public AdminBootstrap(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,
                          @Value("${app.security.bootstrap-admin.enabled:true}") boolean enabled,
                          @Value("${app.security.bootstrap-admin.username:admin}") String username,
                          @Value("${app.security.bootstrap-admin.password:admin123}") String password) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (!enabled || utilisateurRepository.count() > 0 || utilisateurRepository.existsByUsername(username)) {
            return;
        }
        Utilisateur admin = new Utilisateur();
        admin.setNom("Administrateur");
        admin.setPrenom("Garage");
        admin.setTelephone("000000000");
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(RoleUser.ADMIN);
        utilisateurRepository.save(admin);
    }
}
