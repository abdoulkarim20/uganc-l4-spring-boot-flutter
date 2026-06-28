const accessParams = new URLSearchParams(window.location.search);
const accessFeedback = document.querySelector("[data-access-feedback]");

if (accessParams.get("garageRequest") === "sent" && accessFeedback) {
    accessFeedback.textContent = "Demande envoyée. Votre garage est en attente de validation.";
    accessFeedback.hidden = false;
    window.history.replaceState({}, "", "/client/access");
}
