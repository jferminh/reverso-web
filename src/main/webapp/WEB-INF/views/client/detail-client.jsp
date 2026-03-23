<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>

<div class="d-flex align-items-center justify-content-between pb-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/app?cmd=listClients" class="btn btn-outline-secondary btn-sm">
        ← Retour à la liste
    </a>
    <div class="text-center">
        <p class="m-0 fw-bold fs-5">${client.raisonSociale}</p>
        <p class="m-0 small text-muted">Fiche client détaillée</p>
    </div>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/app?cmd=editClient&id=${client.id}" class="btn btn-outline-secondary btn-sm">⚙️ Modifier</a>
    </div>
</div>

<main class="container my-4" id="detail-client"
      data-ville="${client.adresse.ville}"
      data-adresse="${client.adresse.numeroRue} ${client.adresse.nomRue} ${client.adresse.codePostal} ${client.adresse.ville}"
      data-nom="${client.raisonSociale}">

    <section class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h1 class="card-title h5 mb-3">Informations générales</h1>
            <div class="row g-3 align-items-start">

                <div class="col-12 col-sm-2 text-center">
                    <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto" style="width: 64px; height: 64px; font-size: 1.5rem;">
                        ${client.raisonSociale.substring(0, 1).toUpperCase()}
                    </div>
                </div>

                <div class="col-12 col-sm-5">
                    <ul class="list-unstyled mb-0">
                        <li class="mb-1">📧 <a href="mailto:${client.email}">${client.email}</a></li>
                        <li class="mb-1">📞 <a href="tel:${client.telephone}">${client.telephone}</a></li>
                        <li class="mb-1">💼 CA annuel : <strong><fmt:formatNumber value="${client.chiffreAffaires}" type="currency" currencySymbol="€" maxFractionDigits="0"/></strong></li>
                        <li class="mb-1">👥 Employés : <strong>${client.nbEmployes}</strong></li>
                    </ul>
                </div>

                <div class="col-12 col-sm-5">
                    <address class="mb-1">
                        🏙️ ${client.adresse.numeroRue} ${client.adresse.nomRue}<br>
                        ${client.adresse.codePostal} ${client.adresse.ville}
                    </address>
                </div>
            </div>
        </div>
    </section>

    <section class="card mb-4 shadow-sm border-0" id="section-meteo">
        <div class="card-header bg-white d-flex align-items-center gap-2">
            <span>🌤️</span> <h2 class="h5 mb-0">Météo – <span id="meteo-ville">${client.adresse.ville}</span></h2>
        </div>
        <div class="card-body" id="meteo-contenu">
            <div id="meteo-chargement" class="text-center text-secondary py-3">
                <div class="spinner-border spinner-border-sm me-2" role="status"></div> Récupération de la météo…
            </div>
            <div id="meteo-erreur" class="alert alert-warning d-none" role="alert">⚠️ Impossible de récupérer la météo.</div>

            <div id="meteo-donnees" class="d-none">
                <div class="row text-center g-3">
                    <div class="col-6 col-md-3">
                        <div class="fs-1" id="meteo-icone">–</div>
                        <div class="fw-bold" id="meteo-description">–</div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="text-secondary small">Température</div>
                        <div class="fs-4 fw-bold"><span id="meteo-temperature">–</span>°C</div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="text-secondary small">Vent</div>
                        <div class="fs-4 fw-bold"><span id="meteo-vent">–</span><span class="fs-6">km/h</span></div>
                    </div>
                    <div class="col-6 col-md-3">
                        <div class="text-secondary small">Humidité</div>
                        <div class="fs-4 fw-bold"><span id="meteo-humidite">–</span><span class="fs-6">%</span></div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="card mb-4 shadow-sm border-0" id="section-carte">
        <div class="card-header bg-white d-flex align-items-center gap-2">
            <span>🗺️</span> <h2 class="h5 mb-0">Localisation</h2>
        </div>
        <div class="card-body p-0">
            <div id="carte-chargement" class="text-center text-secondary py-3">
                <div class="spinner-border spinner-border-sm me-2" role="status"></div> Récupération de la position…
            </div>
            <div id="carte-erreur" class="alert alert-warning m-3 d-none" role="alert">⚠️ Impossible de localiser cette adresse.</div>
            <div id="carte-leaflet" style="height: 350px;" class="d-none"></div>
        </div>
    </section>
</main>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
<script src="${pageContext.request.contextPath}/assets/js/meteo.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/carte.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/detail-init.js"></script>

<%@ include file="../common/footer.jsp" %>