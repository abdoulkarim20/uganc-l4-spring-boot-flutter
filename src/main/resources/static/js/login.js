const form = document.querySelector(".login-form");
const error = document.querySelector("[data-login-error]");

validateExistingSession();

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    error.hidden = true;

    const data = new FormData(form);
    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: data.get("username"),
                password: data.get("password")
            })
        });

        if (!response.ok) {
            throw new Error(await extractLoginError(response));
        }

        const authResponse = await response.json();
        setAuthSession(authResponse);
        if (isPasswordChangeRequired()) {
            window.location.href = "/change-password";
            return;
        }
        if (isAdminSession()) {
            window.location.href = "/dashboard";
            return;
        }
        if (isClientSession()) {
            window.location.href = "/client/dashboard";
            return;
        }
        if (isMecanicienSession()) {
            window.location.href = "/mecanicien/dashboard";
            return;
        }
        window.location.href = "/403";
    } catch (exception) {
        error.textContent = exception.message;
        error.hidden = false;
    }
});

async function extractLoginError(response) {
    const text = await response.text();
    if (!text) {
        return "Connexion impossible.";
    }
    try {
        const payload = JSON.parse(text);
        if (payload.message) {
            return payload.message;
        }
        return "Connexion impossible.";
    } catch (exception) {
        return text;
    }
}

async function validateExistingSession() {
    const token = getAuthToken();
    if (!token) {
        return;
    }

    try {
        const response = await fetch("/api/auth/me", {
            headers: {
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (getAuthToken() === token) {
                clearAuthSession();
            }
            return;
        }

        const user = await response.json();
        if (getAuthToken() !== token) {
            return;
        }
        localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
            username: user.username,
            roles: user.roles || [],
            mustChangePassword: Boolean(user.mustChangePassword)
        }));

        if (isPasswordChangeRequired()) {
            window.location.href = "/change-password";
            return;
        }
        if (isAdminSession()) {
            window.location.href = "/dashboard";
            return;
        }
        if (isClientSession()) {
            window.location.href = "/client/dashboard";
            return;
        }
        if (isMecanicienSession()) {
            window.location.href = "/mecanicien/dashboard";
        }
    } catch (exception) {
        if (getAuthToken() === token) {
            clearAuthSession();
        }
    }
}
