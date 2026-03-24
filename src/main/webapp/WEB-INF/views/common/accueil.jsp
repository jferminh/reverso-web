<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<%-- TITRE --%>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="mb-1 fw-bold">🏠 Tableau de bord</h2>
        <p class="text-muted mb-0">Bienvenue sur votre espace de gestion.</p>
    </div>
</div>

<%-- ALERTE SUCCÈS (Le filet de sécurité gère déjà les erreurs en rouge) --%>
<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
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
                <div class="mb-3"><span aria-hidden="true" style="font-size: 3rem;">👥</span></div>
                <h5 class="card-title fw-bold">Clients</h5>
                <p class="card-text text-muted">Gérez vos clients actifs et consultez leur chiffre d'affaires.</p>
                <p class="mb-3">
                    <span class="badge bg-primary fs-6">${nbClients} client(s)</span>
                </p>
                <a href="${pageContext.request.contextPath}/app?cmd=listClients" class="btn btn-primary w-100">Gérer les clients</a>
            </div>
        </div>
    </div>

    <%-- Carte Prospects (Nouvelle) --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3"><span aria-hidden="true" style="font-size: 3rem;">🎯</span></div>
                <h5 class="card-title fw-bold">Prospects</h5>
                <p class="card-text text-muted">Suivez vos opportunités commerciales et convertissez-les.</p>
                <p class="mb-3">
                    <span class="badge bg-primary fs-6">${nbProspects} prospect(s)</span>
                </p>
                <a href="${pageContext.request.contextPath}/app?cmd=listProspects" class="btn btn-outline-primary w-100">Gérer les prospects</a>
            </div>
        </div>
    </div>

    <%-- Carte Actions Rapides --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3"><span aria-hidden="true" style="font-size: 3rem;">⚡</span></div>
                <h5 class="card-title fw-bold">Actions rapides</h5>
                <p class="card-text text-muted">Ajoutez rapidement une nouvelle entité dans le système.</p>
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/app?cmd=createClient" class="btn btn-success">+ Nouveau Client</a>
                    <%-- On prépare déjà le lien pour le futur formulaire prospect --%>
                    <a href="${pageContext.request.contextPath}/app?cmd=createProspect" class="btn btn-outline-success">+ Nouveau Prospect</a>
                </div>
            </div>
        </div>
    </div>

</div>

<%@ include file="footer.jsp" %>