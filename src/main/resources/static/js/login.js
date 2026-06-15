const form = document.querySelector(".login-form");
const error = document.querySelector("[data-login-error]");

if (getAuthToken() && isAdminSession()) {
    window.location.href = "/dashboard";
}

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
            throw new Error("Identifiants incorrects");
        }

        const authResponse = await response.json();
        setAuthSession(authResponse);
        if (!isAdminSession()) {
            window.location.href = "/403";
            return;
        }
        window.location.href = "/dashboard";
    } catch (exception) {
        error.textContent = exception.message;
        error.hidden = false;
    }
});
