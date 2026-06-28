const garageRequestForm = document.getElementById("garageRequestForm");
const garageRequestSuccess = document.getElementById("garageRequestSuccess");
const garageSteps = Array.from(document.querySelectorAll("[data-step]"));
const garageIndicators = Array.from(document.querySelectorAll("[data-step-target]"));
const nextButton = document.querySelector("[data-garage-next]");
const previousButton = document.querySelector("[data-garage-prev]");
const submitButton = garageRequestForm?.querySelector('button[type="submit"]');
const backLink = document.querySelector("[data-garage-back-link]");
let currentStep = 1;

nextButton?.addEventListener("click", () => {
    if (!validateCurrentStep()) {
        return;
    }
    setGarageStep(2);
});

previousButton?.addEventListener("click", () => setGarageStep(1));

garageIndicators.forEach((indicator) => {
    indicator.addEventListener("click", () => {
        const targetStep = Number(indicator.dataset.stepTarget);
        if (targetStep > currentStep && !validateCurrentStep()) {
            return;
        }
        setGarageStep(targetStep);
    });
});

garageRequestForm?.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!validateCurrentStep()) {
        return;
    }
    submitGarageRequest();
});

function setGarageStep(step) {
    currentStep = step;
    garageSteps.forEach((section) => {
        section.hidden = Number(section.dataset.step) !== currentStep;
    });
    garageIndicators.forEach((indicator) => {
        const indicatorStep = Number(indicator.dataset.stepTarget);
        indicator.classList.toggle("active", indicatorStep === currentStep);
        indicator.classList.toggle("completed", indicatorStep < currentStep);
    });

    backLink.hidden = currentStep !== 1;
    previousButton.hidden = currentStep === 1;
    nextButton.hidden = currentStep !== 1;
    submitButton.hidden = currentStep !== 2;
    garageRequestSuccess.hidden = true;
}

function validateCurrentStep() {
    const currentSection = document.querySelector(`[data-step="${currentStep}"]`);
    const fields = Array.from(currentSection.querySelectorAll("input, textarea, select"));
    const invalidField = fields.find((field) => !field.checkValidity());
    if (invalidField) {
        invalidField.reportValidity();
        invalidField.focus();
        return false;
    }
    return true;
}

async function submitGarageRequest() {
    const initialLabel = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Envoi...";
    garageRequestSuccess.hidden = true;
    garageRequestSuccess.classList.remove("error");

    try {
        const payload = Object.fromEntries(new FormData(garageRequestForm).entries());
        payload.latitude = payload.latitude ? Number(payload.latitude) : null;
        payload.longitude = payload.longitude ? Number(payload.longitude) : null;
        const response = await fetch("/api/public/garage-registration", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(await extractGarageRequestError(response));
        }

        garageRequestForm.reset();
        setGarageStep(1);
        garageRequestSuccess.textContent = "Demande enregistrée. Votre garage est en attente de validation.";
        garageRequestSuccess.hidden = false;
        window.setTimeout(() => {
            window.location.href = "/client/access?garageRequest=sent";
        }, 1800);
    } catch (error) {
        garageRequestSuccess.textContent = error.message;
        garageRequestSuccess.classList.add("error");
        garageRequestSuccess.hidden = false;
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = initialLabel;
    }
}

async function extractGarageRequestError(response) {
    const text = await response.text();
    if (!text) {
        return "Demande impossible.";
    }
    try {
        const payload = JSON.parse(text);
        return payload.message || "Demande impossible.";
    } catch (error) {
        return text;
    }
}
