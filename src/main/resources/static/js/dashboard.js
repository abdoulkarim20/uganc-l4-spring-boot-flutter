const endpoints = {
    clients: "/api/clients",
    vehicules: "/api/vehicules",
    mecaniciens: "/api/mecaniciens",
    reparations: "/api/reparations"
};

const state = {
    clients: [],
    vehicules: [],
    mecaniciens: [],
    reparations: []
};

const moneyFormat = new Intl.NumberFormat("fr-FR", {
    style: "currency",
    currency: "GNF",
    maximumFractionDigits: 0
});

if (requireAdminSession()) {
    wireLogoutLinks();
    loadDashboard();
}

document.querySelector(".sidebar-toggle").addEventListener("click", () => {
    document.body.classList.toggle("menu-open");
});

document.querySelectorAll(".menu-link").forEach((link) => {
    link.addEventListener("click", () => document.body.classList.remove("menu-open"));
});

async function loadDashboard() {
    const entries = await Promise.allSettled(
        Object.entries(endpoints).map(async ([name, url]) => [name, await fetchCollection(url)])
    );

    entries.forEach((entry) => {
        if (entry.status === "fulfilled") {
            const [name, data] = entry.value;
            state[name] = Array.isArray(data) ? data : [];
        }
    });

    renderDashboard();
}

async function fetchCollection(url) {
    const response = await authFetch(url);
    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }
    return response.json();
}

async function extractErrorMessage(response) {
    const text = await response.text();
    if (!text) {
        return `Erreur HTTP ${response.status}`;
    }
    try {
        const payload = JSON.parse(text);
        return payload.message || text;
    } catch (error) {
        return text;
    }
}

function renderDashboard() {
    setText("clientsCount", state.clients.length);
    setText("vehiculesCount", state.vehicules.length);
    setText("reparationsCount", countActiveRepairs());
    setText("repairTotalCost", moneyFormat.format(totalRepairs()));
    renderRepairs();
    renderMechanics();
}

function renderRepairs() {
    const target = document.getElementById("repairsTable");
    const latest = [...state.reparations]
        .sort((a, b) => String(b.dateReparation || "").localeCompare(String(a.dateReparation || "")))
        .slice(0, 5);

    if (!latest.length) {
        target.innerHTML = `<tr><td colspan="5" class="empty">Aucune réparation trouvée</td></tr>`;
        return;
    }

    target.innerHTML = latest.map((repair) => `
        <tr>
            <td>
                <strong>${escapeHtml(vehiclePlate(repair.vehiculeId))}</strong>
                <span class="muted-line">${escapeHtml(vehicleModel(repair.vehiculeId))}</span>
            </td>
            <td>${escapeHtml(clientNameForRepair(repair))}</td>
            <td>${escapeHtml(mechanicName(repair.mecanicienId))}</td>
            <td>${statusBadge(repair.statut)}</td>
            <td>${moneyFormat.format(Number(repair.cout || 0))}</td>
        </tr>
    `).join("");
}

function renderMechanics() {
    const target = document.getElementById("mechanicsList");
    const rows = state.mecaniciens.slice(0, 4);

    if (!rows.length) {
        target.innerHTML = `<div class="empty">Aucun mécanicien trouvé</div>`;
        return;
    }

    target.innerHTML = rows.map((mechanic) => `
        <div class="team-item">
            <div class="avatar">${escapeHtml(initials(mechanic))}</div>
            <div>
                <strong>${escapeHtml(fullName(mechanic))}</strong>
                <span>${escapeHtml(mechanic.specialite || "Généraliste")} - ${repairCountForMechanic(mechanic.id)} dossier(s)</span>
            </div>
        </div>
    `).join("");
}

function countActiveRepairs() {
    return state.reparations.filter((repair) => {
        const status = String(repair.statut || "").toLowerCase();
        return status.includes("cours") || status.includes("attente") || status.includes("plan");
    }).length;
}

function totalRepairs() {
    return state.reparations.reduce((sum, repair) => sum + Number(repair.cout || 0), 0);
}

function vehiclePlate(id) {
    const vehicle = state.vehicules.find((item) => Number(item.id) === Number(id));
    return vehicle?.immatriculation || "Véhicule";
}

function vehicleModel(id) {
    const vehicle = state.vehicules.find((item) => Number(item.id) === Number(id));
    return vehicle ? `${vehicle.marque || ""} ${vehicle.modele || ""}`.trim() || "-" : "-";
}

function clientNameForRepair(repair) {
    const vehicle = state.vehicules.find((item) => Number(item.id) === Number(repair.vehiculeId));
    const client = vehicle ? state.clients.find((item) => Number(item.id) === Number(vehicle.clientId)) : null;
    return client ? fullName(client) : "-";
}

function mechanicName(id) {
    const mechanic = state.mecaniciens.find((item) => Number(item.id) === Number(id));
    return mechanic ? fullName(mechanic) : "-";
}

function repairCountForMechanic(id) {
    return state.reparations.filter((repair) => Number(repair.mecanicienId) === Number(id)).length;
}

function fullName(person) {
    return `${person.prenom || ""} ${person.nom || ""}`.trim() || "Sans nom";
}

function initials(person) {
    const first = (person.prenom || "").trim()[0] || "";
    const last = (person.nom || "").trim()[0] || "";
    return `${first}${last}`.toUpperCase() || "MC";
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

function setText(id, value) {
    document.getElementById(id).textContent = value;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
