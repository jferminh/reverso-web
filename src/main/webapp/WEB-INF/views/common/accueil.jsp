<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<%-- TITRE --%>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="mb-1 fw-bold">🏠 Tableau de bord</h2>
        <p class="text-muted mb-0">Bienvenue sur votre espace de gestion.</p>
    </div>
</div>

<%-- GESTION DES MESSAGES D'ALERTE (Succès ou Erreur) --%>
<c:if test="${not empty erreurMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ❌ ${erreurMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
    </div>
</c:if>

<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ✅ ${sessionScope.successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
    </div>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<%-- CARTES DE NAVIGATION --%>
<div class="row g-4 mb-5">
    <%-- Carte Clients --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3"><span style="font-size: 3rem;">👥</span></div>
                <h5 class="card-title fw-bold">Clients</h5>
                <p class="card-text text-muted">Gérez vos clients : création, modification et suppression.</p>
                <p class="mb-3">
                    <span class="badge bg-primary fs-6">${nbClients} client(s)</span>
                </p>
                <a href="${pageContext.request.contextPath}/app?cmd=listClients" class="btn btn-primary btn-action">Voir la liste</a>
            </div>
        </div>
    </div>

    <%-- Carte Création rapide --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3"><span style="font-size: 3rem;">➕</span></div>
                <h5 class="card-title fw-bold">Nouveau client</h5>
                <p class="card-text text-muted">Enregistrez un nouveau client ou prospect directement.</p>
                <br>
                <a href="${pageContext.request.contextPath}/app?cmd=createClient" class="btn btn-success btn-action">Créer</a>
            </div>
        </div>
    </div>

    <%-- Carte Info & Futures API (Météo/Map) --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3"><span style="font-size: 3rem;">🌍</span></div>
                <h5 class="card-title fw-bold">Services Tiers</h5>
                <p class="card-text text-muted" id="meteo-status">
                    Préparation de la météo et de la carte Leaflet...
                </p>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>