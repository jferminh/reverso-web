<%--
  Created by IntelliJ IDEA.
  User: CDA-08
  Date: 17/03/2026
  Time: 15:01
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Reverso CRM — ${not empty pageTitle ? pageTitle : 'Accueil'}</title>

    <!--
      CSS compilé depuis SASS via NPM.
      Chemin : /reverso-web/static/css/main.css
      contextPath = /reverso-web (le nom de ton WAR déployé dans Tomcat)
    -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/main.css"/>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">

        <a class="navbar-brand"
           href="${pageContext.request.contextPath}/app">
            🔄 Reverso CRM
        </a>

        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse"
                data-bs-target="#navbarMenu"
                aria-controls="navbarMenu"
                aria-expanded="false"
                aria-label="Menu de navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarMenu">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link text-white"
                       href="${pageContext.request.contextPath}/app?cmd=listClients">
                        👥 Clients
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white"
                       href="${pageContext.request.contextPath}/app?cmd=listProspects">
                        🎯 Prospects
                    </a>
                </li>
                <li class="nav-item ms-2">
                    <a class="nav-link text-white-50"
                       href="${pageContext.request.contextPath}/app?cmd=logout">
                        🚪 Déconnexion
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<main class="container mt-4">