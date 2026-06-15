package gn.uganc.gestiongarage.business.auth.dtos;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        List<String> roles,
        boolean mustChangePassword
) {
}
