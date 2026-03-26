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

    <!-- ============= SECTION 1 : INFORMATIONS SOCIÉTÉ =========== -->
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

    <%-- ================= SECTION 1.5 : CONTRATS ================= --%>
    <section class="card mb-4 shadow-sm border-0" id="section-contrats">
        <div class="card-header bg-white d-flex align-items-center justify-content-between gap-2">
            <div class="d-flex align-items-center gap-2">
                <span aria-hidden="true">🤝</span> <h2 class="h5 mb-0">Contrats commerciaux</h2>
            </div>
            <%-- Bouton pour ouvrir la modale d'ajout --%>
            <button type="button" class="btn btn-primary btn-sm" onclick="ouvrirModalContrat()">
                + Nouveau contrat
            </button>
        </div>
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty client.contrats}">
                    <div class="p-4 text-center text-muted">
                        <p class="mb-0">Aucun contrat n'est actuellement associé à ce client.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                            <tr>
                                <th>Nom du contrat</th>
                                <th class="text-end">Montant</th>
                                <th class="text-end">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:set var="totalContrats" value="0" />
                            <c:forEach var="contrat" items="${client.contrats}">
                                <c:set var="totalContrats" value="${totalContrats + contrat.montant}" />
                                <tr>
                                    <td ><c:out value="${contrat.nomContrat}" /></td>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${contrat.montant}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                    </td>
                                    <td class="text-end">
                                            <%-- Bouton Modifier --%>
                                        <button class="btn btn-sm btn-outline-secondary me-1"
                                                onclick="ouvrirModalContrat(${contrat.id}, '<c:out value="${contrat.nomContrat}"/>', ${contrat.montant})"
                                                title="Modifier">
                                            ✏️
                                        </button>
                                            <%-- Bouton Supprimer --%>
                                        <button class="btn btn-sm btn-outline-danger"
                                                onclick="preparerSuppressionContrat(${contrat.id})"
                                                title="Supprimer">
                                            🗑️
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                            <tfoot class="table-light fw-bold">
                            <tr>
                                <td>Total</td>
                                <td class="text-end text-success">
                                    <fmt:formatNumber value="${totalContrats}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                </td>
                                <td></td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <%-- ================++= SECTION 2 : METEO ==================== --%>
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

    <%-- ================== SECTION 3 : CARTE ===================== --%>
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

<%-- ================= MODALE AJOUT/MODIFICATION CONTRAT ================= --%>
<div class="modal fade" id="modal-contrat" tabindex="-1" aria-labelledby="modal-contrat-titre" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/app" method="POST">
                <input type="hidden" name="cmd" value="saveContrat">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="clientId" value="${client.id}">
                <input type="hidden" name="id" id="input-contrat-id">

                <div class="modal-header">
                    <h2 class="modal-title h5" id="modal-contrat-titre">Nouveau Contrat</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="input-contrat-nom" class="form-label fw-bold">Nom du contrat <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="input-contrat-nom" name="nomContrat" required maxlength="100" placeholder="Ex: Maintenance Annuelle">
                    </div>
                    <div class="mb-3">
                        <label for="input-contrat-montant" class="form-label fw-bold">Montant (€) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="input-contrat-montant" name="montant" required min="0.01" step="0.01" placeholder="Ex: 1500.50">
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-primary">💾 Sauvegarder</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- ================= MODALE SUPPRESSION CONTRAT ================= --%>
<div class="modal fade" id="modal-suppression-contrat" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/app" method="POST">
                <input type="hidden" name="cmd" value="deleteContrat">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="clientId" value="${client.id}">
                <input type="hidden" name="id" id="input-delete-contrat-id">

                <div class="modal-header">
                    <h2 class="modal-title h5">⚠️ Supprimer le contrat</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
                </div>
                <div class="modal-body text-center py-4">
                    <p class="fs-5 text-danger mb-2">Êtes-vous sûr de vouloir supprimer ce contrat ?</p>
                    <p class="text-muted small mb-0">Cette action est irréversible.</p>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-danger">🗑️ Confirmer la suppression</button>
                </div>
            </form>
        </div>
    </div>
</div>
<%-- Import du script externe pour la gestion des contrats --%>
<script src="${pageContext.request.contextPath}/assets/js/contrats.js"></script>
<%@ include file="../common/footer.jsp" %>