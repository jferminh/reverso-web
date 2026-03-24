<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Inclusion des tags JSTL standard pour Jakarta EE --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Inclusion de l'en-tête commun (qui charge Bootstrap et ton CSS) --%>
<%@ include file="header.jsp" %>

<main class="container d-flex flex-column align-items-center justify-content-center flex-grow-1 my-5">

    <div class="card shadow border-danger" style="max-width: 600px; width: 100%;">

        <div class="card-header bg-danger text-white text-center py-3">
            <h1 class="h4 mb-0">
                <span aria-hidden="true" class="fs-3 me-2">⚠️</span>
                Oups ! Une erreur est survenue.
            </h1>
        </div>

        <div class="card-body text-center p-4">

            <p class="card-text text-muted mb-4">
                Nous avons rencontré un problème lors du traitement de votre demande.
            </p>

            <div class="alert alert-warning text-start" role="alert">
                <strong>Détail de l'erreur :</strong><br>
                <c:choose>
                    <c:when test="${not empty erreurMessage}">
                        <%-- c:out est vital ici pour bloquer les attaques XSS si le message contient du code HTML injecté --%>
                        <span class="text-danger fw-medium"><c:out value="${erreurMessage}"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="text-danger fw-medium">Erreur technique inconnue. Veuillez contacter le support.</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="mt-4 d-flex justify-content-center gap-3">
                <button type="button" class="btn btn-outline-secondary" onclick="history.back()">
                    ← Retour
                </button>
                <a href="${pageContext.request.contextPath}/app?cmd=accueil" class="btn btn-danger">
                    Aller au Tableau de bord
                </a>
            </div>

        </div>
    </div>

</main>

<%-- Inclusion du pied de page commun --%>
<%@ include file="footer.jsp" %>