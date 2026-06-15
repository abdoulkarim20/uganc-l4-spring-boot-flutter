const changePasswordForm = document.getElementById("changePasswordForm");
const changePasswordError = document.querySelector("[data-change-password-error]");

if (!getAuthToken()) {
    window.location.href = "/login";
}

changePasswordForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    changePasswordError.hidden = true;

    const data = new FormData(changePasswordForm);
    const newPassword = data.get("newPassword");
    const confirmPassword = data.get("confirmPassword");

    if (newPassword !== confirmPassword) {
        showChangePasswordError("La confirmation ne correspond pas au nouveau mot de passe.");
        return;
    }

    try {
        const response = await authFetch("/api/auth/change-password", {
            method: "POST",
            body: JSON.stringify({
                currentPassword: data.get("currentPassword"),
                newPassword,
                confirmPassword
            })
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || `Erreur HTTP ${response.status}`);
        }

        updatePasswordChangeState(false);
        redirectAfterPasswordChange();
    } catch (error) {
        showChangePasswordError(error.message);
    }
});

function showChangePasswordError(message) {
    changePasswordError.textContent = message;
    changePasswordError.hidden = false;
}
