const registerForm = document.getElementById("clientRegisterForm");
const registerError = document.querySelector("[data-register-error]");
const registerButton = registerForm.querySelector('button[type="submit"]');

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    registerError.hidden = true;
    const initialLabel = registerButton.textContent;
    registerButton.disabled = true;
        registerButton.textContent = "Création...";

    try {
        const payload = Object.fromEntries(new FormData(registerForm).entries());

        const response = await fetch("/api/public/client-registration", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(await extractRegisterError(response));
        }

        showRegisterToast("Compte créé. Vous pouvez vous connecter.", "success");
        registerForm.reset();
        window.setTimeout(() => {
            window.location.href = `/login?redirect=${encodeURIComponent("/client/dashboard")}`;
        }, 1300);
    } catch (error) {
        registerError.textContent = error.message;
        registerError.hidden = false;
    } finally {
        registerButton.disabled = false;
        registerButton.textContent = initialLabel;
    }
});

async function extractRegisterError(response) {
    const text = await response.text();
    if (!text) {
        return "Inscription impossible.";
    }
    try {
        const payload = JSON.parse(text);
        return payload.message || "Inscription impossible.";
    } catch (error) {
        return text;
    }
}

function showRegisterToast(message, type = "") {
    const toast = document.getElementById("registerToast");
    toast.textContent = message;
    toast.className = `toast ${type}`.trim();
    toast.hidden = false;
}
