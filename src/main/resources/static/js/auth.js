const AUTH_TOKEN_KEY = "garagix.accessToken";
const AUTH_USER_KEY = "garagix.user";
const AUTH_LAST_ACTIVITY_KEY = "garagix.lastActivityAt";
const AUTH_INACTIVITY_TIMEOUT_MS = 10 * 60 * 1000;
let inactivityTimer = null;
let lastActivityWriteAt = 0;

function getAuthToken() {
    if (isSessionInactiveExpired()) {
        clearAuthSession();
        return null;
    }
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function setAuthSession(authResponse) {
    localStorage.setItem(AUTH_TOKEN_KEY, authResponse.accessToken);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        username: authResponse.username,
        roles: authResponse.roles || [],
        mustChangePassword: Boolean(authResponse.mustChangePassword)
    }));
    markSessionActivity(true);
}

function clearAuthSession() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
    localStorage.removeItem(AUTH_LAST_ACTIVITY_KEY);
    clearTimeout(inactivityTimer);
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
        window.location.href = loginUrlForCurrentRoute();
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
        window.location.href = loginUrlForCurrentRoute();
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
        window.location.href = loginUrlForCurrentRoute();
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
        window.location.href = loginUrlForCurrentRoute();
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
    scheduleInactivityLogout();
    document.querySelectorAll("[data-logout]").forEach((link) => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            clearAuthSession();
            window.location.href = "/";
        });
    });
}

function initializeInactivityWatcher() {
    ["click", "keydown", "mousemove", "mousedown", "scroll", "touchstart"].forEach((eventName) => {
        window.addEventListener(eventName, () => markSessionActivity(), {passive: true});
    });
    scheduleInactivityLogout();
}

function markSessionActivity(force = false) {
    if (!localStorage.getItem(AUTH_TOKEN_KEY)) {
        return;
    }
    const now = Date.now();
    if (!force && now - lastActivityWriteAt < 15000) {
        return;
    }
    lastActivityWriteAt = now;
    localStorage.setItem(AUTH_LAST_ACTIVITY_KEY, String(now));
    scheduleInactivityLogout();
}

function isSessionInactiveExpired() {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (!token) {
        return false;
    }
    const lastActivityAt = Number(localStorage.getItem(AUTH_LAST_ACTIVITY_KEY) || 0);
    return lastActivityAt > 0 && Date.now() - lastActivityAt > AUTH_INACTIVITY_TIMEOUT_MS;
}

function scheduleInactivityLogout() {
    clearTimeout(inactivityTimer);
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (!token) {
        return;
    }
    const lastActivityAt = Number(localStorage.getItem(AUTH_LAST_ACTIVITY_KEY) || Date.now());
    const remaining = AUTH_INACTIVITY_TIMEOUT_MS - (Date.now() - lastActivityAt);
    if (remaining <= 0) {
        expireSessionByInactivity();
        return;
    }
    inactivityTimer = setTimeout(expireSessionByInactivity, remaining);
}

function expireSessionByInactivity() {
    if (!isSessionInactiveExpired()) {
        scheduleInactivityLogout();
        return;
    }
    clearAuthSession();
    if (!["/", "/login"].includes(window.location.pathname)) {
        window.location.href = loginUrlForCurrentRoute();
    }
}

function loginUrlForCurrentRoute() {
    const target = `${window.location.pathname}${window.location.search}`;
    return `/login?redirect=${encodeURIComponent(target)}`;
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

initializeInactivityWatcher();
