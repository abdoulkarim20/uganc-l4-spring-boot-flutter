const pathParts = window.location.pathname.split("/").filter(Boolean);
const resource = pathParts[0] || "clients";
const itemId = Number(pathParts[1]);

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

document.querySelector(".sidebar-toggle").addEventListener("click", () => {
    document.body.classList.toggle("menu-open");
});

document.querySelector(`[data-menu="${resource}"]`)?.classList.add("active");

loadDetail();

async function loadDetail() {
    await loadCollections();
    const item = state[resource].find((entry) => Number(entry.id) === itemId) || await requestJson(`${endpoints[resource]}/${itemId}`);
    renderDetail(item);
    renderRelated(item);
}

async function loadCollections() {
    const entries = await Promise.allSettled(Object.entries(endpoints).map(async ([name, url]) => [name, await requestJson(url)]));
    entries.forEach((entry) => {
        if (entry.status === "fulfilled") {
            const [name, data] = entry.value;
            state[name] = Array.isArray(data) ? data : [];
        }
    });
}

function renderDetail(item) {
    const config = detailConfig(resource, item);
    setText("pageEyebrow", config.eyebrow);
    setText("pageTitle", config.title);
    setText("detailEyebrow", config.eyebrow);
    setText("detailTitle", config.detailTitle);
    document.getElementById("backLink").href = `/${resource}`;
    document.getElementById("editLink").href = `/${resource}/${itemId}/edit`;
    document.getElementById("detailFields").innerHTML = config.fields.map(([label, value, isHtml]) => `
        <div class="detail-item">
            <span>${escapeHtml(label)}</span>
            <strong>${isHtml ? value : escapeHtml(value ?? "-")}</strong>
        </div>
    `).join("");
}

function renderRelated(item) {
    if (resource === "mecaniciens") {
        const repairs = state.reparations.filter((repair) => Number(repair.mecanicienId) === Number(item.id));
        renderRelatedTable("Réparations du mécanicien", "Atelier", ["Véhicule", "Date", "Statut", "Montant"], repairs, (repair) => [
            vehicleName(repair.vehiculeId),
            formatDate(repair.dateReparation),
            statusBadge(repair.statut),
            moneyFormat.format(Number(repair.cout || 0))
        ], [false, false, true, false]);
        return;
    }

    if (resource === "clients") {
        const vehicles = state.vehicules.filter((vehicle) => Number(vehicle.clientId) === Number(item.id));
        renderRelatedTable("Véhicules du client", "Parc auto", ["Immatriculation", "Marque", "Modèle", "Année"], vehicles, (vehicle) => [
            vehicle.immatriculation,
            vehicle.marque,
            vehicle.modele,
            vehicle.annee
        ]);
        return;
    }

    if (resource === "vehicules") {
        const repairs = state.reparations.filter((repair) => Number(repair.vehiculeId) === Number(item.id));
        renderRelatedTable("Réparations du véhicule", "Atelier", ["Date", "Technicien", "Statut", "Montant"], repairs, (repair) => [
            formatDate(repair.dateReparation),
            mechanicName(repair.mecanicienId),
            statusBadge(repair.statut),
            moneyFormat.format(Number(repair.cout || 0))
        ], [false, false, true, false]);
    }
}

function renderRelatedTable(title, eyebrow, headers, rows, mapper, htmlColumns = []) {
    const panel = document.getElementById("relatedPanel");
    panel.hidden = false;
    setText("relatedEyebrow", eyebrow);
    setText("relatedTitle", title);
    document.getElementById("relatedHead").innerHTML = `<tr>${headers.map((header) => `<th>${escapeHtml(header)}</th>`).join("")}</tr>`;
    document.getElementById("relatedBody").innerHTML = rows.length ? rows.map((row) => `
        <tr>
            ${mapper(row).map((value, index) => `<td>${htmlColumns[index] ? value : escapeHtml(value ?? "-")}</td>`).join("")}
        </tr>
    `).join("") : `<tr><td colspan="${headers.length}" class="empty">Aucune donnée trouvée</td></tr>`;
}

function detailConfig(name, item) {
    if (name === "clients") {
        return {
            eyebrow: "Relation client",
            title: fullName(item),
            detailTitle: "Fiche client",
            fields: [
                ["Nom", item.nom],
                ["Prénom", item.prenom],
                ["Téléphone", item.telephone],
                ["Adresse", item.adresse],
                ["Véhicules", state.vehicules.filter((vehicle) => Number(vehicle.clientId) === Number(item.id)).length]
            ]
        };
    }
    if (name === "vehicules") {
        return {
            eyebrow: "Parc automobile",
            title: item.immatriculation || "Véhicule",
            detailTitle: "Fiche véhicule",
            fields: [
                ["Immatriculation", item.immatriculation],
                ["Marque", item.marque],
                ["Modèle", item.modele],
                ["Année", item.annee],
                ["Client", clientName(item.clientId)]
            ]
        };
    }
    if (name === "mecaniciens") {
        return {
            eyebrow: "Équipe atelier",
            title: fullName(item),
            detailTitle: "Fiche mécanicien",
            fields: [
                ["Nom", item.nom],
                ["Prénom", item.prenom],
                ["Téléphone", item.telephone],
                ["Spécialité", item.specialite],
                ["Réparations", state.reparations.filter((repair) => Number(repair.mecanicienId) === Number(item.id)).length]
            ]
        };
    }
    return {
        eyebrow: "Atelier",
        title: `Réparation #${item.id}`,
        detailTitle: "Fiche réparation",
        fields: [
            ["Date", formatDate(item.dateReparation)],
            ["Véhicule", vehicleName(item.vehiculeId)],
            ["Mécanicien", mechanicName(item.mecanicienId)],
            ["Statut", statusBadge(item.statut), true],
            ["Montant", moneyFormat.format(Number(item.cout || 0))],
            ["Description", item.description]
        ]
    };
}

async function requestJson(url) {
    const response = await fetch(url, {headers: {"Accept": "application/json"}});
    if (!response.ok) {
        throw new Error(`Erreur HTTP ${response.status}`);
    }
    return response.json();
}

function clientName(id) {
    const client = state.clients.find((entry) => Number(entry.id) === Number(id));
    return client ? fullName(client) : "-";
}

function mechanicName(id) {
    const mechanic = state.mecaniciens.find((entry) => Number(entry.id) === Number(id));
    return mechanic ? fullName(mechanic) : "-";
}

function vehicleName(id) {
    const vehicle = state.vehicules.find((entry) => Number(entry.id) === Number(id));
    return vehicle ? `${vehicle.immatriculation || ""} ${vehicle.marque || ""} ${vehicle.modele || ""}`.trim() : "-";
}

function fullName(person) {
    return `${person.prenom || ""} ${person.nom || ""}`.trim() || "Sans nom";
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

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
