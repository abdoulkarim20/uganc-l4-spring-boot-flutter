package gn.uganc.gestiongarage.security;

import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PasswordChangeRequiredFilter extends OncePerRequestFilter {

    private final UtilisateurRepository utilisateurRepository;

    public PasswordChangeRequiredFilter(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/") || uri.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        utilisateurRepository.findByUsername(authentication.getName())
                .filter(utilisateur -> utilisateur.isMustChangePassword())
                .ifPresent(utilisateur -> {
                    response.setStatus(428);
                    response.setContentType("text/plain;charset=UTF-8");
                    try {
                        response.getWriter().write("Changement de mot de passe requis");
                    } catch (IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                });

        if (response.isCommitted()) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
