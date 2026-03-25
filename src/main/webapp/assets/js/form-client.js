/**
 * @file form-client.js
 * @description Orchestrateur principal de la vue Client.
 * Connecte les modules de validation, de brouillon (LocalStorage) et de géolocalisation.
 * @author Julio
 */

document.addEventListener("DOMContentLoaded", () => {

    // Dictionnaire des messages d'erreur accessibles (RGAA)
    const messagesErreur = {
        "raison-sociale": {
            valueMissing: "La raison sociale est obligatoire pour identifier le client.",
            tooShort: "Veuillez saisir au moins 2 caractères."
        },
        "email-client": {
            valueMissing: "L'adresse email est requise pour la facturation.",
            typeMismatch: "Format invalide (attendu : nom@domaine.com).",
        },
        "telephone": {
            valueMissing: "Le numéro de contact est obligatoire.",
            patternMismatch: "Le numéro doit comporter exactement 10 chiffres (sans espaces).",
        },
        "ca-annuel": {
            valueMissing: "Le chiffre d'affaires est requis.",
            rangeUnderflow: "Le montant ne peut pas être inférieur à 200€.",
        },
        "nb-employes": {
            valueMissing: "Le nombre d'employés est obligatoire.",
            rangeUnderflow: "L'entreprise doit comporter au minimum 1 employé.",
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
        "consentement-client": {
            valueMissing: "Vous devez consentir au traitement de vos données pour valider ce formulaire (RGPD).",
        },
    };

    // Appel de l'orchestrateur générique (DRY)
    initialiserFormulaire({
        idForm: "form-client",
        cleBrouillon: "crm-brouillon-client",
        messagesErreur: messagesErreur
    });
});