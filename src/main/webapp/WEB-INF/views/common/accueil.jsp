<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil — Reverso CRM</title>
</head>
<body>
<h1>Bienvenue sur Reverso CRM</h1>
<p>Gestion des Clients et Prospects</p>

<nav>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/app?cmd=listClients">
                Clients
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/app?cmd=listProspects">
                Prospects
            </a>
        </li>
    </ul>
</nav>
</body>
</html>
