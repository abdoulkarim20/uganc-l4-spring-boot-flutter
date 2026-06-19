const AUTH_TOKEN_KEY = "garagix.accessToken";
const AUTH_USER_KEY = "garagix.user";

function getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function setAuthSession(authResponse) {
    localStorage.setItem(AUTH_TOKEN_KEY, authResponse.accessToken);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        username: authResponse.username,
        roles: authResponse.roles || [],
        mustChangePassword: Boolean(authResponse.mustChangePassword)
    }));
}

function clearAuthSession() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
}

function isAdminSession() {
    try {
        const user = getAuthUser();
        return Array.isArray(user.roles) && (
            user.roles.includes("ROLE_ADMIN") || user.roles.includes("ROLE_ADMIN_GARAGE")
        );
    } catch (error) {
        return false;
    }
}

function isPlatformAdminSession() {
    const user = getAuthUser();
    return Array.isArray(user.roles) && user.roles.includes("ROLE_ADMIN");
}

function isGarageAdminSession() {
    const user = getAuthUser();
    return Array.isArray(user.roles) && user.roles.includes("ROLE_ADMIN_GARAGE");
}

function isClientSession() {
    const user = getAuthUser();
    return Array.isArray(user.roles) && user.roles.includes("ROLE_CLIENT");
}

function isMecanicienSession() {
    const user = getAuthUser();
    return Array.isArray(user.roles) && user.roles.includes("ROLE_MECANICIEN");
}

function isPasswordChangeRequired() {
    return Boolean(getAuthUser().mustChangePassword);
}

function updatePasswordChangeState(required) {
    const user = getAuthUser();
    user.mustChangePassword = Boolean(required);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
}

function redirectAfterPasswordChange() {
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
    window.location.href = "/";
}

function requireClientSession() {
    if (!getAuthToken()) {
        window.location.href = "/login";
        return false;
    }
    if (!isClientSession() && !isAdminSession()) {
        window.location.href = "/403";
        return false;
    }
    if (isPasswordChangeRequired() && window.location.pathname !== "/change-password") {
        window.location.href = "/change-password";
        return false;
    }
    return true;
}

function requireMecanicienSession() {
    if (!getAuthToken()) {
        window.location.href = "/login";
        return false;
    }
    if (!isMecanicienSession() && !isAdminSession()) {
        window.location.href = "/403";
        return false;
    }
    if (isPasswordChangeRequired() && window.location.pathname !== "/change-password") {
        window.location.href = "/change-password";
        return false;
    }
    return true;
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
    if (isPasswordChangeRequired() && window.location.pathname !== "/change-password") {
        window.location.href = "/change-password";
        return false;
    }
    return true;
}

async function authFetch(url, options = {}) {
    const {suppressForbiddenRedirect = false, ...fetchOptions} = options;
    const token = getAuthToken();
    const response = await fetch(url, {
        ...fetchOptions,
        headers: {
            "Accept": "application/json",
            ...(fetchOptions.body ? {"Content-Type": "application/json"} : {}),
            ...(token ? {"Authorization": `Bearer ${token}`} : {}),
            ...(fetchOptions.headers || {})
        }
    });

    if (response.status === 401) {
        clearAuthSession();
        window.location.href = "/login";
        throw new Error("Session expirée");
    }

    if (response.status === 403 && !suppressForbiddenRedirect) {
        window.location.href = "/403";
        throw new Error("Accès refusé");
    }

    if (response.status === 428) {
        updatePasswordChangeState(true);
        window.location.href = "/change-password";
        throw new Error("Changement de mot de passe requis");
    }

    return response;
}

function wireLogoutLinks() {
    renderAuthenticatedUser();
    renderRoleNavigation();
    document.querySelectorAll("[data-logout]").forEach((link) => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            clearAuthSession();
            window.location.href = "/";
        });
    });
}

function renderRoleNavigation() {
    if (!isGarageAdminSession()) {
        return;
    }
    const hiddenPaths = ["/garages", "/clients", "/vehicules", "/utilisateurs"];
    document.querySelectorAll("a[href]").forEach((link) => {
        const href = link.getAttribute("href") || "";
        if (hiddenPaths.some((path) => href === path || href.startsWith(`${path}/`))) {
            link.hidden = true;
        }
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
