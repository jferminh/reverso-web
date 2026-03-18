<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="taglibs.jsp" %>
<%@ include file="header.jsp" %>

<%-- ===== TITRE ===== --%>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="mb-1 fw-bold">🏠 Tableau de bord</h2>
        <p class="text-muted mb-0">Bienvenue sur Reverso CRM</p>
    </div>
</div>

<%-- ===== MESSAGE DE SUCCÈS (ex: après login) ===== --%>
<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ✅ ${sessionScope.successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"
                aria-label="Fermer"></button>
    </div>
    <%-- On efface le message après affichage --%>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<%-- ===== CARTES DE NAVIGATION ===== --%>
<div class="row g-4 mb-5">

    <%-- Carte Clients --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3">
                    <span style="font-size: 3rem;">👥</span>
                </div>
                <h5 class="card-title fw-bold">Clients</h5>
                <p class="card-text text-muted">
                    Gérez vos clients : création, modification et suppression.
                </p>

                <%-- Badge avec le nombre de clients (chargé par AccueilCommand) --%>
                <c:if test="${nbClients != null}">
                    <p class="mb-3">
                        <span class="badge bg-primary fs-6">${nbClients} client(s)</span>
                    </p>
                </c:if>

                <a href="${pageContext.request.contextPath}/app?cmd=listClients"
                   class="btn btn-primary btn-action">
                    Voir la liste
                </a>
            </div>
        </div>
    </div>

    <%-- Carte Accès rapide : Créer un client --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3">
                    <span style="font-size: 3rem;">➕</span>
                </div>
                <h5 class="card-title fw-bold">Nouveau client</h5>
                <p class="card-text text-muted">
                    Enregistrez un nouveau client directement.
                </p>
                <a href="${pageContext.request.contextPath}/app?cmd=createClient"
                   class="btn btn-success btn-action">
                    Créer
                </a>
            </div>
        </div>
    </div>

    <%-- Carte Info : À propos du projet --%>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm">
            <div class="card-body text-center py-4">
                <div class="mb-3">
                    <span style="font-size: 3rem;">ℹ️</span>
                </div>
                <h5 class="card-title fw-bold">Reverso CRM</h5>
                <p class="card-text text-muted">
                    Application Jakarta EE · Bootstrap 5 · Maven
                    <br/>AFPA CDA — ECF 2026
                </p>
                <span class="badge bg-secondary">v1.0.0</span>
            </div>
        </div>
    </div>

</div>

<%-- ===== RACCOURCIS RAPIDES ===== --%>
<div class="card border-0 shadow-sm">
    <div class="card-header bg-light fw-bold">
        ⚡ Raccourcis
    </div>
    <div class="card-body">
        <div class="d-flex flex-wrap gap-2">
            <a href="${pageContext.request.contextPath}/app?cmd=listClients"
               class="btn btn-outline-primary">
                👥 Liste clients
            </a>
            <a href="${pageContext.request.contextPath}/app?cmd=createClient"
               class="btn btn-outline-success">
                ➕ Nouveau client
            </a>
            <a href="${pageContext.request.contextPath}/app?cmd=logout"
               class="btn btn-outline-secondary">
                🚪 Déconnexion
            </a>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
