if (!requireClientSession()) {
    throw new Error("Authentification client requise");
}

wireLogoutLinks();

const moneyFormatClient = new Intl.NumberFormat("fr-FR", {
    style: "currency",
    currency: "GNF",
    maximumFractionDigits: 0
});

document.getElementById("showVehicleForm")?.addEventListener("click", () => {
    document.getElementById("clientVehicleForm").hidden = false;
    document.getElementById("showVehicleForm").hidden = true;
});

document.getElementById("hideVehicleForm")?.addEventListener("click", () => {
    document.getElementById("clientVehicleForm").reset();
    document.getElementById("clientVehicleForm").hidden = true;
    document.getElementById("showVehicleForm").hidden = false;
});

document.getElementById("clientVehicleForm")?.addEventListener("submit", saveClientVehicle);

loadClientDashboard();

async function loadClientDashboard() {
    try {
        const data = await requestClientJson("/api/client-space/dashboard");
        renderClientDashboard(data);
    } catch (error) {
        showToast(error.message, "error");
        document.getElementById("clientRepairs").innerHTML = `<tr><td class="empty" colspan="5">${escapeHtml(error.message)}</td></tr>`;
    }
}

function renderClientDashboard(data) {
    const client = data.client || {};
    const vehicles = data.vehicules || [];
    const repairs = data.reparations || [];

    setText("clientName", `${client.prenom || ""} ${client.nom || ""}`.trim() || "Bienvenue");
    setText("clientVehiclesCount", vehicles.length);
    setText("clientActiveRepairsCount", data.reparationsEnCours || 0);
    setText("clientTotalSpent", moneyFormatClient.format(Number(data.totalDepenses || 0)));

    document.getElementById("clientVehicles").innerHTML = vehicles.length ? vehicles.map((vehicle) => `
        <div class="client-vehicle-card">
            <span><i class="bi bi-car-front" aria-hidden="true"></i></span>
            <div>
                <strong>${escapeHtml(vehicle.immatriculation || "Véhicule")}</strong>
                <small>${escapeHtml(`${vehicle.marque || ""} ${vehicle.modele || ""}`.trim() || "-")}</small>
                <em>${escapeHtml(vehicle.annee || "-")}</em>
            </div>
        </div>
    `).join("") : `<p class="empty">Aucun véhicule trouvé.</p>`;

    document.getElementById("clientRepairs").innerHTML = repairs.length ? repairs.map((repair) => `
        <tr>
            <td>${escapeHtml(repair.vehicule || "-")}</td>
            <td>${escapeHtml(repair.mecanicien || "-")}</td>
            <td>${statusBadge(repair.statut)}</td>
            <td>${moneyFormatClient.format(Number(repair.cout || 0))}</td>
            <td>${formatDate(repair.dateReparation)}</td>
        </tr>
    `).join("") : `<tr><td class="empty" colspan="5">Aucune réparation trouvée.</td></tr>`;
}

async function requestClientJson(url, options = {}) {
    const response = await authFetch(url, options);
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Erreur HTTP ${response.status}`);
    }
    return response.json();
}

async function saveClientVehicle(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector('button[type="submit"]');
    const initialLabel = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Enregistrement...";

    try {
        const payload = Object.fromEntries(new FormData(form).entries());
        payload.annee = payload.annee ? Number(payload.annee) : null;
        await requestClientJson("/api/client-space/vehicules", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        showToast("Véhicule ajouté.", "success");
        form.reset();
        form.hidden = true;
        document.getElementById("showVehicleForm").hidden = false;
        await loadClientDashboard();
    } catch (error) {
        showToast(error.message, "error");
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = initialLabel;
    }
}

function statusBadge(status) {
    return `<span class="status ${statusClass(status)}">${escapeHtml(status || "Planifiée")}</span>`;
}

function statusClass(status = "") {
    const normalized = status.toLowerCase();
    if (normalized.includes("term")) {
        return "done";
    }
    if (normalized.includes("annul")) {
        return "cancelled";
    }
    if (normalized.includes("cours")) {
        return "progress";
    }
    return "";
}

function formatDate(value) {
    return value ? new Intl.DateTimeFormat("fr-FR").format(new Date(`${value}T00:00:00`)) : "-";
}

function setText(id, value) {
    document.getElementById(id).textContent = value;
}

function showToast(message, type = "") {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = `toast ${type}`.trim();
    toast.hidden = false;
    window.setTimeout(() => {
        toast.hidden = true;
    }, 3500);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
