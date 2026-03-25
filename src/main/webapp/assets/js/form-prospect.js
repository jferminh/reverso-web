/**
 * @file form-prospect.js
 * @description Orchestrateur principal de la vue Prospect.
 * Connecte les modules de validation, de brouillon et de géolocalisation.
 * @author Julio
 */

document.addEventListener("DOMContentLoaded", () => {

    // Dictionnaire des messages d'erreur spécifiques au modèle Prospect
    const messagesErreur = {
        "raison-sociale": {
            valueMissing: "La raison sociale est obligatoire pour identifier le prospect.",
            tooShort: "Veuillez saisir au moins 2 caractères."
        },
        "email-prospect": {
            valueMissing: "L'adresse email est requise pour contacter le prospect.",
            typeMismatch: "Format invalide (attendu : nom@domaine.com).",
        },
        "telephone": {
            valueMissing: "Le numéro de contact est obligatoire.",
            patternMismatch: "Le numéro doit comporter exactement 10 chiffres (sans espaces).",
        },
        "date-prospection": {
            valueMissing: "La date de prospection est obligatoire.",
            rangeOverflow: "La date ne peut pas être dans le futur." // Optionnel: dépend de ta config JS
        },
        "interesse": {
            valueMissing: "Veuillez sélectionner le niveau d'intérêt de ce prospect.",
        },
        "numero-rue": {
            valueMissing: "Le numéro de voie est obligatoire.",
        },
        "rue": {
            valueMissing: "Le nom de la rue/voie est obligatoire.",
        },
        "code-postal": {
            valueMissing: "Le code postal est requis.",
            patternMismatch: "Le code postal doit comporter exactement 5 chiffres.",
        },
        "ville": {
            valueMissing: "La ville est obligatoire.",
        },
        "consentement-prospect": {
            valueMissing: "Vous devez consentir au traitement des données pour valider.",
        },
    };

    // Appel de l'orchestrateur générique (DRY)
    initialiserFormulaire({
        idForm: "form-prospect",
        cleBrouillon: "crm-brouillon-prospect",
        messagesErreur: messagesErreur
    });
});