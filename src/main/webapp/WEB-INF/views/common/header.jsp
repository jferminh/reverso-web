<%@ include file="taglibs.jsp" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${not empty pageTitle ? pageTitle : 'Reverso CRM'}</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css" />
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4 shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/app?cmd=accueil">
            🏢 Reverso CRM
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu" aria-controls="navMenu" aria-expanded="false" aria-label="Navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navMenu">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/app?cmd=accueil">🏠 Accueil</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/app?cmd=listClients">👥 Clients</a>
                </li>
            </ul>

            <ul class="navbar-nav ms-auto align-items-center">
                <c:if test="${not empty sessionScope.utilisateurActif}">
                    <li class="nav-item me-3 text-white opacity-75 small">
                        👤 Connecté : <strong>${sessionScope.utilisateurActif}</strong>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/app?cmd=logout">
                            🚪 Déconnexion
                        </a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

<main class="container mb-5">