package gn.uganc.gestiongarage.business.auth.dtos;

import java.util.List;

public record MeResponse(String username, List<String> roles, boolean mustChangePassword) {
}
