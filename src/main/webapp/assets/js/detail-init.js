/**
 * detail-init.js
 * Orchestrateur de la page de détails (Client ou Prospect).
 * Il lit les données injectées par le serveur Java dans les attributs data-* du <main>.
 */
document.addEventListener("DOMContentLoaded", async function () {

    // 💡 Astuce DRY : On cherche le conteneur du client OU du prospect
    const mainElement = document.getElementById("detail-client") || document.getElementById("detail-prospect");
    // const mainElement = document.getElementById("detail-client");
    if (!mainElement) return;

    // 2. On lit les données que Java (JSTL) a glissées dans les attributs data-*
    const ville = mainElement.getAttribute("data-ville");
    const adresseComplete = mainElement.getAttribute("data-adresse");
    const nomEntite = mainElement.getAttribute("data-nom");

    // 3. Lancement asynchrone (En parallèle !)
    await Promise.all([

        // --- LANCEMENT DE LA METEO ---
        (async () => {
            if (ville && ville.trim() !== "" && typeof lancerMeteo === "function") {
                await lancerMeteo(ville);
            } else {
                document.getElementById("section-meteo")?.classList.add("d-none");
            }
        })(),

        // --- LANCEMENT DE LA CARTE ---
        (async () => {
            if (adresseComplete && adresseComplete.trim() !== "" && typeof geocoderAdresse === "function") {
                afficherEtatCartChargement();

                // On géocode l'adresse
                const coords = await geocoderAdresse(adresseComplete);

                if (coords && coords.score >= 0.3) {
                    // On initialise Leaflet (fonction de ton fichier carte.js)
                    initialiserCarte(coords.lat, coords.lon, coords.label, nomEntite);
                } else {
                    afficherEtatCarteErreur();
                }
            } else {
                document.getElementById("section-carte")?.classList.add("d-none");
            }
        })()

    ]);
});