<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<%-- Dépendances Leaflet pour la carte --%>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>

<div class="d-flex align-items-center justify-content-between pb-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/app?cmd=listProspects" class="btn btn-outline-secondary btn-sm">
        ← Retour à la liste
    </a>
    <div class="text-center">
        <p class="m-0 fw-bold fs-5">${prospect.raisonSociale}</p>
        <p class="m-0 small text-muted">Fiche prospect détaillée</p>
    </div>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/app?cmd=editProspect&id=${prospect.id}" class="btn btn-outline-secondary btn-sm">⚙️ Modifier</a>
    </div>
</div>

<%-- L'ID a été changé en 'detail-prospect' --%>
<main class="container my-4" id="detail-prospect"
      data-ville="${prospect.adresse.ville}"
      data-adresse="${prospect.adresse.numeroRue} ${prospect.adresse.nomRue} ${prospect.adresse.codePostal} ${prospect.adresse.ville}"
      data-nom="${prospect.raisonSociale}">

    <%-- ================= SECTION 1 : INFOS GÉNÉRALES ================= --%>
    <section class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h1 class="card-title h5 mb-3">Informations générales</h1>
            <div class="row g-3 align-items-start">

                <%-- Avatar généré avec la 1ère lettre --%>
                <div class="col-12 col-sm-2 text-center">
                    <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto" style="width: 64px; height: 64px; font-size: 1.5rem;">
                        ${prospect.raisonSociale.substring(0, 1).toUpperCase()}
                    </div>
                </div>

                <%-- Coordonnées et Prospection --%>
                <div class="col-12 col-sm-5">
                    <ul class="list-unstyled mb-0">
                        <li class="mb-1">📧 <a href="mailto:${prospect.email}" class="text-decoration-none">${prospect.email}</a></li>
                        <li class="mb-1">📞 <a href="tel:${prospect.telephone}" class="text-decoration-none">${prospect.telephone}</a></li>
                        <li class="mb-1 mt-3">📅 Date de prospection : <strong>${prospect.dateProspection}</strong></li>
                        <li class="mb-1">🎯 Intérêt :
                            <c:choose>
                                <c:when test="${prospect.interesse.name() == 'OUI'}">
                                    <span class="badge bg-success">Fort (OUI)</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Faible (NON)</span>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>

                <%-- Adresse --%>
                <div class="col-12 col-sm-5">
                    <address class="mb-1">
                        🏙️ ${prospect.adresse.numeroRue} ${prospect.adresse.nomRue}<br>
                        ${prospect.adresse.codePostal} ${prospect.adresse.ville}
                    </address>
                </div>
            </div>

            <%-- Commentaires conditionnels (S'affiche uniquement s'il y a un commentaire) --%>
            <c:if test="${not empty prospect.commentaires}">
                <hr class="mt-4 mb-3">
                <div class="row">
                    <div class="col-12">
                        <h3 class="h6 text-muted mb-2">📝 Notes additionnelles</h3>
                        <p class="mb-0 bg-light p-3 rounded border text-break">
                            <c:out value="${prospect.commentaires}" />
                        </p>
                    </div>
                </div>
            </c:if>
        </div>
    </section>

    <%-- ================= SECTION 2 : MÉTÉO ================= --%>
    <section class="card mb-4 shadow-sm border-0" id="section-meteo">
        <div class="card-header bg-white d-flex align-items-center gap-2">
            <span aria-hidden="true">🌤️</span> <h2 class="h5 mb-0">Météo – <span id="meteo-ville">${prospect.adresse.ville}</span></h2>
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

    <%-- ================= SECTION 3 : CARTE ================= --%>
    <section class="card mb-4 shadow-sm border-0" id="section-carte">
        <div class="card-header bg-white d-flex align-items-center gap-2">
            <span aria-hidden="true">🗺️</span> <h2 class="h5 mb-0">Localisation</h2>
        </div>
        <div class="card-body p-0">
            <div id="carte-chargement" class="text-center text-secondary py-3">
                <div class="spinner-border spinner-border-sm me-2" role="status"></div> Récupération de la position…
            </div>
            <div id="carte-erreur" class="alert alert-warning m-3 d-none" role="alert">⚠️ Impossible de localiser cette adresse.</div>
            <div id="carte-leaflet" style="height: 350px;" class="d-none z-1"></div>
        </div>
    </section>
</main>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
<script src="${pageContext.request.contextPath}/assets/js/meteo.js" defer></script>
<script src="${pageContext.request.contextPath}/assets/js/carte.js" defer></script>
<script src="${pageContext.request.contextPath}/assets/js/detail-init.js" defer></script>

<%@ include file="../common/footer.jsp" %>