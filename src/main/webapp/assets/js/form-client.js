/**
 * form-client.js
 * Gère la validation et les événements du formulaire Client.
 */
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-client");
    if (!form) return;

    const CLE_BROUILLON = "brouillon-client";

    const messagesErreur = {
        "raison-sociale": {
            valueMissing: "La raison sociale est obligatoire.",
            tooShort: "Doit contenir au moins 2 caractères."
        },
        "email-client": {
            valueMissing: "L'adresse email est obligatoire.",
            typeMismatch: "Format invalide (ex: contact@societe.fr).",
        },
        "telephone": {
            valueMissing: "Le numéro est obligatoire.",
            patternMismatch: "Doit contenir exactement 10 chiffres.",
        },
        "ca-annuel": {
            valueMissing: "Le chiffre d'affaires est obligatoire.",
            rangeUnderflow: "Minimum 200€.",
        },
        "nb-employes": {
            valueMissing: "Le nombre d'employés est obligatoire.",
            rangeUnderflow: "Minimum 1 employé.",
        },
        "numero-rue": {
            valueMissing: "Le numéro est obligatoire.",
        },
        "rue": {
            valueMissing: "La rue est obligatoire.",
        },
        "code-postal": {
            valueMissing: "Le code postal est obligatoire.",
            patternMismatch: "Doit contenir exactement 5 chiffres.",
        },
        "ville": {
            valueMissing: "La ville est obligatoire.",
        },
        "consentement-client": {
            valueMissing: "Vous devez accepter pour continuer.",
        },
    };

    // ── Branchement des modules externes ──
    if (typeof brancherValidation === "function") brancherValidation(messagesErreur);
    if (typeof brancherBoutonBrouillon === "function") brancherBoutonBrouillon("btn-brouillon", form, CLE_BROUILLON);
    if (typeof brancherAutoSauvegarde === "function") brancherAutoSauvegarde(form, CLE_BROUILLON);
    if (typeof restaurerAvecEtatVisuel === "function") restaurerAvecEtatVisuel(form, messagesErreur, CLE_BROUILLON);

    // ⚠️ IMPORTANT : Ta fonction brancherSoumission doit laisser passer la requête POST si valide
    if (typeof brancherSoumission === "function") brancherSoumission(form, messagesErreur, CLE_BROUILLON);

    // ── API Geo Adresse (Auto-complétion) ──
    if (typeof brancherGeoAdresse === "function") {
        brancherGeoAdresse({
            idNumero: "numero-rue",
            idRue: "rue",
            idCp: "code-postal",
            idVille: "ville",
            idBtnGeo: "btn-geo",
            idSuggestions: "suggestion-adresse",
            messagesErreur: messagesErreur,
        });
    }
});