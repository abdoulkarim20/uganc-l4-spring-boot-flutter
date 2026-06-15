package gn.uganc.gestiongarage.business.auth.dtos;

public record ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
}
