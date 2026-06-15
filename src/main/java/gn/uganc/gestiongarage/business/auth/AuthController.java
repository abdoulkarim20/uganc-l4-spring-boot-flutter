package gn.uganc.gestiongarage.business.auth;

import gn.uganc.gestiongarage.business.auth.dtos.AuthResponse;
import gn.uganc.gestiongarage.business.auth.dtos.ChangePasswordRequest;
import gn.uganc.gestiongarage.business.auth.dtos.LoginRequest;
import gn.uganc.gestiongarage.business.auth.dtos.MeResponse;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Utilisateur utilisateur = findUtilisateur(userDetails.getUsername());

        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), userDetails.getUsername(), roles,
                utilisateur.isMustChangePassword());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Utilisateur utilisateur = findUtilisateur(userDetails.getUsername());
        return new MeResponse(userDetails.getUsername(), roles, utilisateur.isMustChangePassword());
    }

    @PostMapping("/change-password")
    public MeResponse changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody ChangePasswordRequest request) {
        Utilisateur utilisateur = findUtilisateur(userDetails.getUsername());
        if (!passwordEncoder.matches(request.currentPassword(), utilisateur.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect");
        }
        if (!StringUtils.hasText(request.newPassword()) || request.newPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nouveau mot de passe doit contenir au moins 6 caractères");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La confirmation du mot de passe ne correspond pas");
        }
        utilisateur.setPassword(passwordEncoder.encode(request.newPassword()));
        utilisateur.setMustChangePassword(false);
        utilisateurRepository.save(utilisateur);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new MeResponse(userDetails.getUsername(), roles, false);
    }

    private Utilisateur findUtilisateur(String username) {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
