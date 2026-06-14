const pathParts = window.location.pathname.split("/").filter(Boolean);
const resource = pathParts[0] || "clients";
const mode = pathParts.includes("create") ? "create" : pathParts.includes("edit") ? "edit" : "list";
const editId = mode === "edit" ? Number(pathParts[1]) : null;

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

const configs = {
    clients: {
        eyebrow: "Relation client",
        singular: "client",
        title: "Clients",
        listTitle: "Liste des clients",
        createTitle: "Ajouter un client",
        columns: [
            [(item) => fullName(item), "Nom complet"],
            ["telephone", "Téléphone"],
            ["adresse", "Adresse"],
            [(item) => countVehiclesByClient(item.id), "Véhicules"]
        ],
        fields: [
            {name: "nom", label: "Nom", required: true},
            {name: "prenom", label: "Prenom", required: true},
            {name: "telephone", label: "Telephone", required: true},
            {name: "adresse", label: "Adresse"}
        ]
    },
    vehicules: {
        eyebrow: "Parc automobile",
        singular: "véhicule",
        title: "Véhicules",
        listTitle: "Liste des véhicules",
        createTitle: "Ajouter un véhicule",
        columns: [
            ["immatriculation", "Immatriculation"],
            [(item) => `${item.marque || ""} ${item.modele || ""}`.trim(), "Véhicule"],
            ["annee", "Année"],
            [(item) => clientName(item.clientId), "Client"]
        ],
        fields: [
            {name: "immatriculation", label: "Immatriculation", required: true},
            {name: "marque", label: "Marque", required: true},
            {name: "modele", label: "Modele", required: true},
            {name: "annee", label: "Année", type: "number"},
            {name: "clientId", label: "Client", type: "select", required: true, options: () => state.clients.map((item) => [item.id, fullName(item)])}
        ]
    },
    mecaniciens: {
        eyebrow: "Equipe atelier",
        singular: "mécanicien",
        title: "Mécaniciens",
        listTitle: "Liste des mécaniciens",
        createTitle: "Ajouter un mécanicien",
        columns: [
            [(item) => fullName(item), "Nom complet"],
            ["telephone", "Téléphone"],
            ["specialite", "Spécialité"],
            [(item) => countRepairsByMechanic(item.id), "Dossiers"]
        ],
        fields: [
            {name: "nom", label: "Nom", required: true},
            {name: "prenom", label: "Prenom", required: true},
            {name: "telephone", label: "Telephone", required: true},
            {name: "specialite", label: "Specialite", required: true}
        ]
    },
    reparations: {
        eyebrow: "Atelier",
        singular: "réparation",
        title: "Réparations",
        listTitle: "Liste des réparations",
        createTitle: "Ajouter une réparation",
        columns: [
            [(item) => vehicleName(item.vehiculeId), "Véhicule"],
            [(item) => mechanicName(item.mecanicienId), "Technicien"],
            [(item) => statusBadge(item.statut), "Statut", true],
            [(item) => moneyFormat.format(Number(item.cout || 0)), "Montant"],
            [(item) => formatDate(item.dateReparation), "Date"]
        ],
        fields: [
            {name: "dateReparation", label: "Date", type: "date", required: true},
            {name: "description", label: "Description", type: "textarea", full: true},
            {name: "cout", label: "Montant", type: "number", step: "0.01", required: true},
            {
                name: "statut",
                label: "Statut",
                type: "select",
                required: true,
                options: () => [["planifiee", "Planifiée"], ["en_cours", "En cours"], ["terminee", "Terminée"], ["annulee", "Annulée"]]
            },
            {name: "vehiculeId", label: "Véhicule", type: "select", required: true, options: () => state.vehicules.map((item) => [item.id, vehicleName(item.id)])},
            {name: "mecanicienId", label: "Mécanicien", type: "select", required: true, options: () => state.mecaniciens.map((item) => [item.id, fullName(item)])}
        ]
    }
};

document.querySelector(".sidebar-toggle").addEventListener("click", () => {
    document.body.classList.toggle("menu-open");
});

document.querySelector(`[data-menu="${resource}"]`)?.classList.add("active");
document.getElementById("crudForm").addEventListener("submit", saveRecord);
document.getElementById("tableBody").addEventListener("click", (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }
    if (button.dataset.action === "delete") {
        deleteRecord(Number(button.dataset.id));
    }
});

loadPage();

async function loadPage() {
    await loadCollections();
    if (mode === "list") {
        renderList();
    } else {
        const item = mode === "edit" ? await requestJson(`${endpoints[resource]}/${editId}`) : null;
        renderForm(item);
    }
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

function renderList() {
    const config = configs[resource];
    setHeader(config.eyebrow.replace("Relation client", "Carnet client"), config.title);
    document.getElementById("listView").hidden = false;
    document.getElementById("formView").hidden = true;
    setText("listEyebrow", config.eyebrow);
    setText("listTitle", config.listTitle);
    document.getElementById("addLink").href = `/${resource}/create`;

    document.getElementById("tableHead").innerHTML = `
        <tr>
            ${config.columns.map(([, label]) => `<th>${escapeHtml(label)}</th>`).join("")}
            <th>Actions</th>
        </tr>
    `;

    const items = state[resource];
    document.getElementById("tableBody").innerHTML = items.length ? items.map((item) => `
        <tr>
            ${config.columns.map((column) => renderCell(column, item)).join("")}
            <td>
                <div class="row-actions">
                    <a class="table-action view icon-action" href="/${resource}/${item.id}" aria-label="Voir les détails">
                        <i class="bi bi-eye" aria-hidden="true"></i>
                    </a>
                    <a class="table-action edit icon-action" href="/${resource}/${item.id}/edit" aria-label="Modifier">
                        <i class="bi bi-pencil-square" aria-hidden="true"></i>
                    </a>
                    <button class="table-action delete icon-action" type="button" data-action="delete" data-id="${item.id}" aria-label="Supprimer">
                        <i class="bi bi-trash" aria-hidden="true"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join("") : `<tr><td colspan="${config.columns.length + 1}" class="empty">Aucune donnée trouvée</td></tr>`;
}

function renderForm(item) {
    const config = configs[resource];
    setHeader(config.eyebrow, mode === "edit" ? `Modifier ${config.singular}` : config.createTitle);
    document.getElementById("listView").hidden = true;
    document.getElementById("formView").hidden = false;
    document.getElementById("cancelLink").href = `/${resource}`;
    document.getElementById("formFields").innerHTML = config.fields.map((field) => renderField(field, item)).join("");
}

function renderCell(column, item) {
    const [keyOrGetter, , isHtml] = column;
    const value = typeof keyOrGetter === "function" ? keyOrGetter(item) : item[keyOrGetter];
    return `<td>${isHtml ? value : escapeHtml(value ?? "-")}</td>`;
}

function renderField(field, item) {
    const value = item?.[field.name] ?? defaultValue(field);
    const required = field.required ? "required" : "";
    const full = field.full || field.type === "textarea" ? " full" : "";

    if (field.type === "textarea") {
        return `<div class="form-field${full}"><label for="${field.name}">${field.label}</label><textarea id="${field.name}" name="${field.name}" ${required}>${escapeHtml(value)}</textarea></div>`;
    }
    if (field.type === "select") {
        return `
            <div class="form-field${full}">
                <label for="${field.name}">${field.label}</label>
                <select id="${field.name}" name="${field.name}" ${required}>
                    <option value="">Sélectionner</option>
                    ${field.options().map(([optionValue, optionLabel]) => `<option value="${escapeHtml(optionValue)}" ${String(optionValue) === String(value) ? "selected" : ""}>${escapeHtml(optionLabel)}</option>`).join("")}
                </select>
            </div>
        `;
    }
    return `<div class="form-field${full}"><label for="${field.name}">${field.label}</label><input id="${field.name}" name="${field.name}" type="${field.type || "text"}" value="${escapeHtml(value)}" ${required} ${field.step ? `step="${field.step}"` : ""}></div>`;
}

async function saveRecord(event) {
    event.preventDefault();
    const config = configs[resource];
    const form = new FormData(event.currentTarget);
    const payload = {};

    config.fields.forEach((field) => {
        let value = form.get(field.name);
        if (field.type === "number") {
            value = value === "" ? null : Number(value);
        }
        if (field.name.endsWith("Id") && value !== "") {
            value = Number(value);
        }
        payload[field.name] = value;
    });

    try {
        await requestJson(mode === "edit" ? `${endpoints[resource]}/${editId}` : endpoints[resource], {
            method: mode === "edit" ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        window.location.href = `/${resource}`;
    } catch (error) {
        const target = document.getElementById("formError");
        target.textContent = error.message;
        target.hidden = false;
    }
}

async function deleteRecord(id) {
    if (!window.confirm("Supprimer cet élément ?")) {
        return;
    }
    try {
        await requestJson(`${endpoints[resource]}/${id}`, {method: "DELETE"});
        showToast("Suppression effectuée", "success");
        await loadCollections();
        renderList();
    } catch (error) {
        showToast(error.message, "error");
    }
}

async function requestJson(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Erreur HTTP ${response.status}`);
    }
    if (response.status === 204) {
        return null;
    }
    return response.json();
}

function setHeader(eyebrow, title) {
    setText("pageEyebrow", eyebrow);
    setText("pageTitle", title);
}

function setText(id, value) {
    document.getElementById(id).textContent = value;
}

function countVehiclesByClient(id) {
    return state.vehicules.filter((item) => Number(item.clientId) === Number(id)).length;
}

function countRepairsByMechanic(id) {
    return state.reparations.filter((item) => Number(item.mecanicienId) === Number(id)).length;
}

function clientName(id) {
    const client = state.clients.find((item) => Number(item.id) === Number(id));
    return client ? fullName(client) : "-";
}

function mechanicName(id) {
    const mechanic = state.mecaniciens.find((item) => Number(item.id) === Number(id));
    return mechanic ? fullName(mechanic) : "-";
}

function vehicleName(id) {
    const vehicle = state.vehicules.find((item) => Number(item.id) === Number(id));
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

function defaultValue(field) {
    if (field.type === "date") {
        return new Date().toISOString().slice(0, 10);
    }
    if (field.name === "statut") {
        return "planifiee";
    }
    return "";
}

function showToast(message, type = "") {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = `toast ${type}`.trim();
    toast.hidden = false;
    window.setTimeout(() => {
        toast.hidden = true;
    }, 3000);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
