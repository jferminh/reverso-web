/**
 * geo-adresse.js
 * Auto-complétion d'adresse via l'API adresse.data.gouv.fr
 */

const URL_API_ADRESSE = "https://api-adresse.data.gouv.fr/search/";
const NB_SUGGESTIONS = 5;
const DELAI_DEBOUNCE = 400;

async function rechercherAdresse(recherche) {
    if (recherche.trim().length < 3) return [];
    try {
        const response = await fetch(`${URL_API_ADRESSE}?q=${encodeURIComponent(recherche)}&limit=${NB_SUGGESTIONS}`);
        if (!response.ok) return [];
        const donnees = await response.json();
        return donnees.features || [];
    } catch (error) {
        return [];
    }
}

function afficherSuggestions(liste, features, onChoix) {
    liste.innerHTML = '';
    if (features.length === 0) {
        liste.classList.add('d-none');
        return;
    }
    features.forEach(feature => {
        const li = document.createElement("li");
        li.className = "list-group-item list-group-item-action";
        li.setAttribute("role", "option");
        li.setAttribute("tabindex", "0");
        li.textContent = feature.properties.label;

        li.addEventListener("click", () => { onChoix(feature.properties); masquerSuggestions(liste); });
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

function masquerSuggestions(liste) {
    liste.innerHTML = '';
    liste.classList.add('d-none');
}

// ── OPTIMISATION : Extraction du Numéro de Rue ──
function remplirChampsAdresse(props, idNumero, idRue, idCp, idVille, messagesErreur) {
    const champNumero = document.getElementById(idNumero);
    const champRue = document.getElementById(idRue);
    const champCp = document.getElementById(idCp);
    const champVille = document.getElementById(idVille);

    // Si la rue a un numéro, on le met. Sinon on laisse vide.
    if (champNumero) champNumero.value = props.housenumber || "";
    // Si la rue n'a pas de nom (street), on prend le label complet (name)
    if (champRue) champRue.value = props.street || props.name || "";
    if (champCp) champCp.value = props.postcode || "";
    if (champVille) champVille.value = props.city || "";

    [champNumero, champRue, champCp, champVille].forEach(champ => {
        if (champ) afficherErreur(champ, messagesErreur);
    });
}

function brancherGeoAdresse(config) {
    const champRue = document.getElementById(config.idRue);
    const btnGeo = document.getElementById(config.idBtnGeo);
    const liste = document.getElementById(config.idSuggestions);

    if (!champRue || !btnGeo || !liste) return;

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

    let debounceGeo;
    champRue.addEventListener("input", () => {
        clearTimeout(debounceGeo);
        if (champRue.value.trim().length < 3) { masquerSuggestions(liste); return; }

        debounceGeo = setTimeout(async () => {
            const features = await rechercherAdresse(champRue.value.trim());
            afficherSuggestions(liste, features, (props) => {
                remplirChampsAdresse(props, config.idNumero, config.idRue, config.idCp, config.idVille, config.messagesErreur);
            });
        }, DELAI_DEBOUNCE);
    });

    document.addEventListener("click", (e) => {
        if (!liste.contains(e.target) && e.target !== champRue) masquerSuggestions(liste);
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") { masquerSuggestions(liste); champRue.focus(); }
    });
}