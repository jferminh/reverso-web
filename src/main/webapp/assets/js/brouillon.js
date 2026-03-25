/**
 * @file brouillon.js
 * @description Module de gestion de la persistance temporaire (LocalStorage).
 * Permet de sauvegarder et restaurer les saisies utilisateur pour éviter la perte de données.
 * @author Julio
 */

/**
 * Extrait les données utiles d'un formulaire sous forme d'objet clé-valeur.
 * * @param {HTMLFormElement} formulaire - Le formulaire à analyser.
 * @returns {Object} Un objet contenant les paires {name: value}.
 */
function lireFormulaire(formulaire) {
    const donnees = {};
    // Champs à ignorer (RGPD, champs techniques générés par le backend)
    const CHAMPS_EXCLUS = ["consentement", "cmd", "csrfToken", "id", "idAdresse"];

    const elements = formulaire.elements;
    for (let i = 0; i < elements.length; i++) {
        const champ = elements[i];

        // On ignore les boutons et les champs exclus
        if (!champ.name || CHAMPS_EXCLUS.includes(champ.name)) continue;
        if (champ.type === "submit" || champ.type === "button") continue;

        if (champ.type === "checkbox") {
            donnees[champ.name] = champ.checked;
        } else {
            donnees[champ.name] = champ.value.trim(); // Trim pour éviter de sauver juste des espaces
        }
    }
    return donnees;
}

/**
 * Sauvegarde les données dans le navigateur de l'utilisateur.
 * * @param {string} cle - La clé d'identification dans le LocalStorage.
 * @param {Object} donnees - Les données extraites du formulaire.
 */
function sauvegarderBrouillon(cle, donnees) {
    // ÉCO-CONCEPTION : Si le formulaire est totalement vide, on ne sollicite pas le disque dur.
    const estVide = Object.values(donnees).every(val => val === "" || val === false);
    if (estVide) return;

    donnees._sauvegardeLe = new Date().toLocaleTimeString("fr-FR");

    try {
        localStorage.setItem(cle, JSON.stringify(donnees));
    } catch (e) {
        console.warn("Impossible de sauvegarder le brouillon (Quota LocalStorage potentiellement atteint).");
    }
}

/**
 * Affiche un retour visuel accessible (RGAA) confirmant la sauvegarde.
 * * @param {HTMLElement} bouton - Le bouton ayant déclenché l'action.
 */
function afficherConfirmationBrouillon(bouton) {
    let zone = document.getElementById('msg-brouillon');
    if (!zone) {
        zone = document.createElement('small');
        zone.id = 'msg-brouillon';
        zone.className = 'text-success ms-2 fw-bold';
        // RGAA : 'status' et 'polite' forcent le lecteur d'écran à annoncer ce texte dynamiquement
        zone.setAttribute("role", 'status');
        zone.setAttribute('aria-live', 'polite');
        bouton.parentNode.appendChild(zone);
    }
    zone.textContent = '💾 Sauvegardé à ' + new Date().toLocaleTimeString("fr-FR");

    // Nettoyage automatique pour ne pas polluer l'interface
    setTimeout(() => zone.textContent = '', 4000);
}

/**
 * Lit et parse les données sauvegardées.
 * * @param {string} cle - La clé du LocalStorage.
 * @returns {Object|null} Les données ou null si introuvable/corrompu.
 */
function lireBrouillon(cle) {
    const json = localStorage.getItem(cle);
    if (!json) return null;
    try {
        return JSON.parse(json);
    } catch (erreur) {
        // Auto-réparation : si les données sont corrompues, on nettoie
        localStorage.removeItem(cle);
        return null;
    }
}

/**
 * Injecte les données du brouillon dans les champs du formulaire.
 * * @param {HTMLFormElement} formulaire
 * @param {Object} donnees
 */
function restaurerFormulaire(formulaire, donnees) {
    Object.keys(donnees).forEach(cle => {
        if (cle.startsWith("_")) return; // Ignore les métadonnées (ex : _sauvegardeLe)

        const champ = formulaire.querySelector(`[name='${cle}']`);
        if (!champ) return;

        if (champ.type === "checkbox") {
            champ.checked = !!donnees[cle];
        } else {
            champ.value = donnees[cle];
        }
    });
}

/**
 * Supprime le brouillon (ex : après une soumission réussie).
 * * @param {string} cle
 */
function effacerBrouillon(cle) {
    localStorage.removeItem(cle);
}

/**
 * Affiche une bannière informative (RGAA) pour signaler qu'une restauration a eu lieu.
 */
function afficherBanniereBrouillon(formulaire, heureSauvegarde) {
    const banniere = document.createElement("div");
    banniere.className = "alert alert-info alert-dismissible d-flex align-items-center gap-2 mb-4 shadow-sm";
    banniere.setAttribute("role", "alert");
    banniere.innerHTML = `
        <span aria-hidden="true">📄</span>
        <span>Un brouillon non soumis a été restauré (sauvegardé à <strong>${heureSauvegarde}</strong>).</span>
        <button type='button' class='btn-close' data-bs-dismiss='alert' aria-label='Fermer le message'></button>
    `;
    formulaire.insertBefore(banniere, formulaire.firstChild);
}