const AUTH_TOKEN_KEY = "garagix.accessToken";
const AUTH_USER_KEY = "garagix.user";

function getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function setAuthSession(authResponse) {
    localStorage.setItem(AUTH_TOKEN_KEY, authResponse.accessToken);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        username: authResponse.username,
        roles: authResponse.roles || []
    }));
}

function clearAuthSession() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
}

function isAdminSession() {
    try {
        const user = getAuthUser();
        return Array.isArray(user.roles) && user.roles.includes("ROLE_ADMIN");
    } catch (error) {
        return false;
    }
}

function getAuthUser() {
    try {
        return JSON.parse(localStorage.getItem(AUTH_USER_KEY) || "{}");
    } catch (error) {
        return {};
    }
}

function requireAdminSession() {
    if (!getAuthToken()) {
        window.location.href = "/login";
        return false;
    }
    if (!isAdminSession()) {
        window.location.href = "/403";
        return false;
    }
    return true;
}

async function authFetch(url, options = {}) {
    const token = getAuthToken();
    const response = await fetch(url, {
        ...options,
        headers: {
            "Accept": "application/json",
            ...(options.body ? {"Content-Type": "application/json"} : {}),
            ...(token ? {"Authorization": `Bearer ${token}`} : {}),
            ...(options.headers || {})
        }
    });

    if (response.status === 401) {
        clearAuthSession();
        window.location.href = "/login";
        throw new Error("Session expirée");
    }

    if (response.status === 403) {
        window.location.href = "/403";
        throw new Error("Accès refusé");
    }

    return response;
}

function wireLogoutLinks() {
    renderAuthenticatedUser();
    document.querySelectorAll("[data-logout]").forEach((link) => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            clearAuthSession();
            window.location.href = "/";
        });
    });
}

function renderAuthenticatedUser() {
    const user = getAuthUser();
    const username = user.username || "Utilisateur";
    const primaryRole = Array.isArray(user.roles) ? user.roles[0] : "";
    const role = formatUserRole(primaryRole);
    const initials = username
        .split(/[.\s_-]+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0])
        .join("")
        .toUpperCase() || "UT";

    document.querySelectorAll("[data-user-name]").forEach((target) => {
        target.textContent = username;
    });
    document.querySelectorAll("[data-user-role]").forEach((target) => {
        target.textContent = role;
    });
    document.querySelectorAll("[data-user-initials]").forEach((target) => {
        target.textContent = initials;
    });
}

function formatUserRole(role = "") {
    const normalized = role.replace(/^ROLE_/, "");
    const labels = {
        ADMIN: "Administrateur",
        ADMIN_GARAGE: "Admin garage",
        MECANICIEN: "Mécanicien",
        CLIENT: "Client"
    };
    return labels[normalized] || normalized || "Profil";
}
