const pathParts = window.location.pathname.split("/").filter(Boolean);
const resource = pathParts[0] || "clients";
const mode = pathParts.includes("create") ? "create" : pathParts.includes("edit") ? "edit" : "list";
const editId = mode === "edit" ? Number(pathParts[1]) : null;
const queryParams = new URLSearchParams(window.location.search);

const endpoints = {
    garages: "/api/garages",
    clients: "/api/clients",
    vehicules: "/api/vehicules",
    mecaniciens: "/api/mecaniciens",
    reparations: "/api/reparations",
    utilisateurs: "/api/utilisateurs"
};

const state = {
    garages: [],
    clients: [],
    vehicules: [],
    mecaniciens: [],
    reparations: [],
    utilisateurs: []
};

const moneyFormat = new Intl.NumberFormat("fr-FR", {
    style: "currency",
    currency: "GNF",
    maximumFractionDigits: 0
});

if (!requireAdminSession()) {
    throw new Error("Authentification requise");
}

wireLogoutLinks();

const configs = {
    garages: {
        eyebrow: "Structures",
        singular: "garage",
        title: "Garages",
        listTitle: "Liste des garages",
        createTitle: "Ajouter un garage",
        columns: [
            ["nom", "Nom"],
            ["telephone", "Telephone"],
            ["ville", "Ville"],
            [(item) => garageStatusLabel(item.statut), "Statut"]
        ],
        fields: [
            {name: "nom", label: "Nom du garage", required: true},
            {name: "telephone", label: "Telephone", required: true},
            {name: "email", label: "Email", type: "email"},
            {name: "adresse", label: "Adresse"},
            {name: "ville", label: "Ville"},
            {name: "quartier", label: "Quartier"},
            {name: "pays", label: "Pays"},
            {name: "nomResponsable", label: "Nom du responsable"},
            {name: "telephoneResponsable", label: "Telephone du responsable"},
            {name: "latitude", label: "Latitude", type: "number", step: "0.000001"},
            {name: "longitude", label: "Longitude", type: "number", step: "0.000001"},
            {
                name: "statut",
                label: "Statut",
                type: "select",
                required: true,
                options: () => [["EN_ATTENTE", "En attente"], ["ACTIF", "Actif"], ["INACTIF", "Inactif"]]
            },
            {name: "description", label: "Description", type: "textarea", full: true}
        ]
    },
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
            {name: "adresse", label: "Adresse"},
            {name: "password", label: "Mot de passe du compte client", type: "password", required: () => mode === "create", full: true, visible: () => mode === "create"}
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
            [(item) => item.garageNom || "-", "Garage"],
            [(item) => countRepairsByMechanic(item.id), "Dossiers"]
        ],
        fields: [
            {name: "nom", label: "Nom", required: true},
            {name: "prenom", label: "Prenom", required: true},
            {name: "telephone", label: "Telephone", required: true},
            {name: "specialite", label: "Specialite", required: true},
            {name: "garageId", label: "Garage", type: "select", required: true, options: () => state.garages.map((item) => [item.id, item.nom])},
            {name: "password", label: "Mot de passe du compte mécanicien", type: "password", required: () => mode === "create", full: true, visible: () => mode === "create"}
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
    },
    utilisateurs: {
        eyebrow: "Sécurité",
        singular: "utilisateur",
        title: "Utilisateurs",
        listTitle: "Liste des utilisateurs",
        createTitle: "Ajouter un utilisateur",
        columns: [
            [(item) => fullName(item), "Nom complet"],
            ["telephone", "Téléphone"],
            ["username", "Identifiant"],
            [(item) => item.garageNom || "-", "Garage"],
            [(item) => roleLabel(item.role), "Profil"]
        ],
        fields: [
            {name: "nom", label: "Nom", required: true},
            {name: "prenom", label: "Prenom", required: true},
            {name: "telephone", label: "Telephone", required: true},
            {name: "email", label: "Email", type: "email"},
            {name: "adresse", label: "Adresse"},
            {name: "username", label: "Identifiant", required: true},
            {name: "password", label: "Mot de passe", type: "password", required: () => mode === "create", full: true},
            {
                name: "role",
                label: "Profil",
                type: "select",
                required: true,
                options: () => [
                    ["ADMIN", "Administrateur plateforme"],
                    ["ADMIN_GARAGE", "Administrateur garage"],
                    ["MECANICIEN", "Mécanicien"],
                    ["CLIENT", "Client"]
                ]
            },
            {
                name: "garageId",
                label: "Garage",
                type: "select",
                full: true,
                options: () => [["", "Aucun garage"], ...state.garages.map((item) => [item.id, item.nom])]
            }
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
    const entries = await Promise.allSettled(Object.entries(endpoints).map(async ([name, url]) => [
        name,
        await requestJson(url, {suppressForbiddenRedirect: name !== resource})
    ]));
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
    document.getElementById("formFields").innerHTML = `${renderFormContext()}${visibleFields(config).map((field) => renderField(field, item)).join("")}`;
}

function renderCell(column, item) {
    const [keyOrGetter, , isHtml] = column;
    const value = typeof keyOrGetter === "function" ? keyOrGetter(item) : item[keyOrGetter];
    return `<td>${isHtml ? value : escapeHtml(value ?? "-")}</td>`;
}

function renderField(field, item) {
    const value = item?.[field.name] ?? defaultValue(field);
    const isRequired = typeof field.required === "function" ? field.required() : field.required;
    const required = isRequired ? "required" : "";
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
    const placeholder = field.type === "password" && mode === "edit" ? "Laisser vide pour conserver le mot de passe actuel" : "";
    return `<div class="form-field${full}"><label for="${field.name}">${field.label}</label><input id="${field.name}" name="${field.name}" type="${field.type || "text"}" value="${escapeHtml(value)}" ${required} ${field.step ? `step="${field.step}"` : ""} ${placeholder ? `placeholder="${escapeHtml(placeholder)}"` : ""}></div>`;
}

async function saveRecord(event) {
    event.preventDefault();
    const config = configs[resource];
    const form = new FormData(event.currentTarget);
    const payload = {};

    visibleFields(config).forEach((field) => {
        let value = form.get(field.name);
        if (field.type === "number") {
            value = value === "" ? null : Number(value);
        }
        if (field.name.endsWith("Id")) {
            value = value === "" ? null : Number(value);
        }
        payload[field.name] = value;
    });

    try {
        const saved = await requestJson(mode === "edit" ? `${endpoints[resource]}/${editId}` : endpoints[resource], {
            method: mode === "edit" ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        if (resource === "vehicules" && mode === "create" && saved?.id) {
            renderVehicleNextStep(saved);
            return;
        }
        window.location.href = `/${resource}`;
    } catch (error) {
        const target = document.getElementById("formError");
        target.textContent = error.message;
        target.hidden = false;
    }
}

function visibleFields(config) {
    return config.fields.filter((field) => !field.visible || field.visible());
}

async function deleteRecord(id) {
    const confirmed = await confirmAction({
        title: "Supprimer cet élément ?",
        message: "Cette action est définitive. Les données liées peuvent aussi être impactées."
    });
    if (!confirmed) {
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
    const response = await authFetch(url, {
        ...options,
        headers: {
            ...(options.headers || {})
        }
    });
    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }
    if (response.status === 204) {
        return null;
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
        const parts = [];
        if (payload.message) {
            parts.push(payload.message);
        }
        if (payload.errors) {
            if (typeof payload.errors === "string") {
                parts.push(payload.errors);
            } else if (Array.isArray(payload.errors)) {
                parts.push(payload.errors.join(", "));
            } else if (typeof payload.errors === "object") {
                parts.push(Object.entries(payload.errors).map(([field, message]) => `${field}: ${message}`).join(" · "));
            }
        }
        return parts.filter(Boolean).join(" — ") || `Erreur HTTP ${response.status}`;
    } catch (error) {
        return text;
    }
}

function confirmAction({title, message}) {
    const overlay = document.getElementById("confirmOverlay");
    const titleTarget = document.getElementById("confirmTitle");
    const messageTarget = document.getElementById("confirmMessage");
    const okButton = overlay.querySelector("[data-confirm-ok]");
    const cancelButton = overlay.querySelector("[data-confirm-cancel]");

    titleTarget.textContent = title;
    messageTarget.textContent = message;
    overlay.hidden = false;
    okButton.focus();

    return new Promise((resolve) => {
        const cleanup = (result) => {
            overlay.hidden = true;
            okButton.removeEventListener("click", onOk);
            cancelButton.removeEventListener("click", onCancel);
            overlay.removeEventListener("click", onOverlay);
            document.removeEventListener("keydown", onKeydown);
            resolve(result);
        };
        const onOk = () => cleanup(true);
        const onCancel = () => cleanup(false);
        const onOverlay = (event) => {
            if (event.target === overlay) {
                cleanup(false);
            }
        };
        const onKeydown = (event) => {
            if (event.key === "Escape") {
                cleanup(false);
            }
        };

        okButton.addEventListener("click", onOk);
        cancelButton.addEventListener("click", onCancel);
        overlay.addEventListener("click", onOverlay);
        document.addEventListener("keydown", onKeydown);
    });
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

function roleLabel(role = "") {
    const labels = {
        ADMIN: "Administrateur plateforme",
        ADMIN_GARAGE: "Administrateur garage",
        MECANICIEN: "Mécanicien",
        CLIENT: "Client"
    };
    return labels[role] || role || "-";
}

function garageStatusLabel(statut = "") {
    const labels = {
        EN_ATTENTE: "En attente",
        ACTIF: "Actif",
        INACTIF: "Inactif"
    };
    return labels[statut] || statut || "-";
}

function formatDate(value) {
    return value ? new Intl.DateTimeFormat("fr-FR").format(new Date(`${value}T00:00:00`)) : "-";
}

function defaultValue(field) {
    const queryValue = queryParams.get(field.name);
    if (queryValue) {
        return queryValue;
    }
    if (field.type === "date") {
        return new Date().toISOString().slice(0, 10);
    }
    if (field.name === "statut") {
        return "planifiee";
    }
    return "";
}

function renderFormContext() {
    if (resource !== "reparations" || mode !== "create") {
        return "";
    }
    const vehicleId = queryParams.get("vehiculeId");
    if (!vehicleId) {
        return "";
    }
    return `
        <div class="form-context full">
            <i class="bi bi-car-front" aria-hidden="true"></i>
            <div>
                <strong>Réparation liée au véhicule</strong>
                <span>${escapeHtml(vehicleName(vehicleId))}</span>
            </div>
        </div>
    `;
}

function renderVehicleNextStep(vehicle) {
    const vehicleLabel = `${vehicle.immatriculation || ""} ${vehicle.marque || ""} ${vehicle.modele || ""}`.trim();
    setHeader("Parc automobile", "Véhicule enregistré");
    document.getElementById("crudForm").innerHTML = `
        <div class="next-step-card">
            <span class="next-step-icon"><i class="bi bi-check2-circle" aria-hidden="true"></i></span>
            <p class="eyebrow">Suite logique</p>
            <h2>${escapeHtml(vehicleLabel || "Le véhicule")} a bien été ajouté.</h2>
            <p>
                Vous pouvez maintenant créer une réparation pour ce véhicule. Cette étape reste optionnelle,
                mais elle permet d’enchaîner directement avec le dossier atelier.
            </p>
            <div class="next-step-actions">
                <a class="primary-button button-link" href="/reparations/create?vehiculeId=${encodeURIComponent(vehicle.id)}">
                    <i class="bi bi-tools" aria-hidden="true"></i>
                    Créer la réparation
                </a>
                <a class="ghost-button button-link" href="/vehicules">Voir les véhicules</a>
            </div>
        </div>
    `;
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
