const healthForm = document.getElementById("vehicleHealthForm");
const healthMessage = document.getElementById("vehicleHealthMessage");
const healthResult = document.getElementById("vehicleHealthResult");

healthForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    healthMessage.hidden = true;
    healthMessage.classList.remove("error");
    healthResult.hidden = true;

    const submitButton = healthForm.querySelector('button[type="submit"]');
    const initialLabel = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Recherche...";

    try {
        const payload = Object.fromEntries(new FormData(healthForm).entries());
        const response = await fetch("/api/public/vehicle-health", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            throw new Error(await extractHealthError(response));
        }
        renderVehicleHealth(await response.json());
    } catch (error) {
        healthMessage.textContent = error.message;
        healthMessage.classList.add("error");
        healthMessage.hidden = false;
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = initialLabel;
    }
});

function renderVehicleHealth(data) {
    healthResult.innerHTML = `
        <div class="health-summary-card">
            <span class="${healthClass(data.niveau)}"><i class="bi bi-heart-pulse" aria-hidden="true"></i></span>
            <div>
                <p class="eyebrow">État actuel</p>
                <h3>${escapeHtml(data.niveau)}</h3>
                <p>${escapeHtml(data.message)}</p>
            </div>
        </div>
        <div class="health-info-grid">
            <div><small>Véhicule</small><strong>${escapeHtml(data.vehicule)}</strong></div>
            <div><small>Immatriculation</small><strong>${escapeHtml(data.immatriculation)}</strong></div>
            <div><small>Propriétaire</small><strong>${escapeHtml(data.proprietaire)}</strong></div>
            <div><small>Total interventions</small><strong>${escapeHtml(data.totalReparations)}</strong></div>
        </div>
        <div class="health-repairs">
            <h3>Interventions et consignes</h3>
            ${data.dernieresReparations?.length ? data.dernieresReparations.map(renderRepair).join("") : '<p class="muted-line">Aucun historique disponible.</p>'}
        </div>
    `;
    healthResult.hidden = false;
}

function renderRepair(repair) {
    return `
        <article>
            <div>
                <strong>${escapeHtml(repair.description || "Intervention")}</strong>
                <span>${escapeHtml(formatDate(repair.date))} · ${escapeHtml(repair.garage || "-")}</span>
                <p class="health-repair-note"><b>Consigne garage :</b> ${escapeHtml(repair.consigneClient || "Aucune consigne enregistrée pour cette intervention.")}</p>
                ${repair.conseil ? `<p class="health-repair-advice"><b>Conseil GARAGIX :</b> ${escapeHtml(repair.conseil)}</p>` : ""}
            </div>
            <em>${escapeHtml(repair.statut || "-")}</em>
        </article>
    `;
}

function healthClass(level = "") {
    if (level.includes("surveiller")) {
        return "health-icon warning";
    }
    if (level.includes("Aucun")) {
        return "health-icon neutral";
    }
    return "health-icon stable";
}

async function extractHealthError(response) {
    const text = await response.text();
    if (!text) {
        return "Consultation impossible.";
    }
    try {
        const payload = JSON.parse(text);
        return payload.message || "Consultation impossible.";
    } catch (error) {
        return text;
    }
}

function formatDate(value) {
    return value ? new Intl.DateTimeFormat("fr-FR").format(new Date(`${value}T00:00:00`)) : "-";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
