/**
 * @file geo-adresse.js
 * @description Auto-complétion d'adresse via l'API publique (adresse.data.gouv.fr).
 * Implémente le pattern Debounce et l'AbortController pour l'éco-conception (optimisation réseau).
 * @author Julio
 */

const URL_API_ADRESSE = "https://api-adresse.data.gouv.fr/search/";
const NB_SUGGESTIONS = 5;
const DELAI_DEBOUNCE = 400;

let controleurRequete = null; // Permet d'annuler les requêtes HTTP obsolètes

/**
 * Interroge l'API gouvernementale avec annulation des requêtes en vol.
 * @param {string} recherche - L'adresse tapée par l'utilisateur.
 * @returns {Promise<Array>} Liste des features (adresses) renvoyées par l'API.
 */
async function rechercherAdresse(recherche) {
    if (recherche.trim().length < 3) return [];

    // ÉCO-CONCEPTION : Annule la requête précédente si l'utilisateur tape encore
    if (controleurRequete) controleurRequete.abort();
    controleurRequete = new AbortController();

    try {
        const response = await fetch(`${URL_API_ADRESSE}?q=${encodeURIComponent(recherche)}&limit=${NB_SUGGESTIONS}`, {
            signal: controleurRequete.signal
        });
        if (!response.ok) return [];
        const donnees = await response.json();
        return donnees.features || [];
    } catch (error) {
        // On ignore l'erreur si elle est due à notre propre annulation (AbortError)
        if (error.name !== 'AbortError') {
            console.error("Erreur de communication avec l'API Adresse :", error);
        }
        return [];
    }
}

/**
 * Construit la liste déroulante des suggestions avec support RGAA.
 */
function afficherSuggestions(liste, features, onChoix) {
    liste.innerHTML = '';

    if (features.length === 0) {
        masquerSuggestions(liste);
        return;
    }

    // RGAA : On annonce le nombre de résultats trouvés au lecteur d'écran
    liste.setAttribute("aria-live", "polite");

    features.forEach(feature => {
        const li = document.createElement("li");
        li.className = "list-group-item list-group-item-action cursor-pointer";
        li.setAttribute("role", "option");
        li.setAttribute("tabindex", "0"); // Permet la navigation au clavier (Tab)
        li.textContent = feature.properties.label;

        // Événement Clic (Souris)
        li.addEventListener("click", () => {
            onChoix(feature.properties);
            masquerSuggestions(liste);
        });

        // Événement Clavier (Accessibilité RGAA)
        li.addEventListener("keydown", (e) => {
            if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                onChoix(feature.properties);
                masquerSuggestions(liste);
            }
        });
        liste.appendChild(li);
    });

    liste.classList.remove('d-none');
}

/**
 * Vide et cache la liste de suggestions.
 */
function masquerSuggestions(liste) {
    liste.innerHTML = '';
    liste.classList.add('d-none');
}

/**
 * Dispatche les données de l'API dans les champs du formulaire.
 */
function remplirChampsAdresse(props, idNumero, idRue, idCp, idVille, messagesErreur) {
    const champNumero = document.getElementById(idNumero);
    const champRue = document.getElementById(idRue);
    const champCp = document.getElementById(idCp);
    const champVille = document.getElementById(idVille);

    // Extraction intelligente des propriétés
    if (champNumero) champNumero.value = props.housenumber || "";
    if (champRue) champRue.value = props.street || props.name || "";
    if (champCp) champCp.value = props.postcode || "";
    if (champVille) champVille.value = props.city || "";

    // On déclenche la validation visuelle pour effacer les éventuelles erreurs rouges
    [champNumero, champRue, champCp, champVille].forEach(champ => {
        if (champ && typeof afficherErreur === "function") {
            afficherErreur(champ, messagesErreur);
        }
    });
}

/**
 * Initialise le module d'auto-complétion sur les champs du DOM.
 */
function brancherGeoAdresse(config) {
    const champRue = document.getElementById(config.idRue);
    const btnGeo = document.getElementById(config.idBtnGeo);
    const liste = document.getElementById(config.idSuggestions);

    if (!champRue || !btnGeo || !liste) return;

    // Recherche au clic sur le bouton
    btnGeo.addEventListener("click", async () => {
        const valeur = champRue.value.trim();
        if (valeur.length < 3) { champRue.focus(); return; }

        btnGeo.textContent = "⏳";
        btnGeo.disabled = true;

        const features = await rechercherAdresse(valeur);

        btnGeo.textContent = "📍";
        btnGeo.disabled = false;

        afficherSuggestions(liste, features, (props) => {
            remplirChampsAdresse(props, config.idNumero, config.idRue, config.idCp, config.idVille, config.messagesErreur);
        });
    });

    // Recherche automatique pendant la frappe (Debounce)
    let debounceGeo;
    champRue.addEventListener("input", () => {
        clearTimeout(debounceGeo);
        if (champRue.value.trim().length < 3) {
            masquerSuggestions(liste);
            return;
        }

        debounceGeo = setTimeout(async () => {
            const features = await rechercherAdresse(champRue.value.trim());
            afficherSuggestions(liste, features, (props) => {
                remplirChampsAdresse(props, config.idNumero, config.idRue, config.idCp, config.idVille, config.messagesErreur);
            });
        }, DELAI_DEBOUNCE);
    });

    // Fermeture de la liste si on clique ailleurs (UX)
    document.addEventListener("click", (e) => {
        if (!liste.contains(e.target) && e.target !== champRue) masquerSuggestions(liste);
    });

    // Fermeture de la liste via la touche Échap (RGAA)
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") {
            masquerSuggestions(liste);
            champRue.focus();
        }
    });
}