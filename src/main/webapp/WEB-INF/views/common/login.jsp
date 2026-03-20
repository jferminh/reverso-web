<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="description"
          content="SwingApp CRM — Gestion clients et prospects. Connectez-vous pour accéder à votre tableau de bord." />
    <title>SwingApp CRM — Connexion</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css" />
</head>
<body class="bg-light">

<div id="rgpd-banner"
     class="position-fixed bottom-0 start-0 end-0 z-3 bg-dark text-white p-3 shadow-lg"
     role="dialog" aria-modal="true" aria-label="Bandeau de consentement"
     hidden>
    <div class="container d-flex flex-wrap align-items-center justify-content-between gap-3">
        <p class="mb-0 small">
            <strong>SwingApp CRM</strong> utilise le <strong>LocalStorage</strong>
            pour mémoriser vos saisies. Données supprimées après
            <strong>30 jours sans activité</strong>.
            <a href="${pageContext.request.contextPath}/mentions-legales"
               class="text-white-50 text-decoration-underline ms-1">En savoir plus</a>
        </p>
        <div class="d-flex gap-2 flex-shrink-0">
            <button type="button" id="btn-rgpd-refuser"
                    class="btn btn-outline-light btn-sm">Refuser</button>
            <button type="button" id="btn-rgpd-accepter"
                    class="btn btn-success btn-sm">Accepter</button>
        </div>
    </div>
</div>

<main>
    <section class="hero text-white" aria-labelledby="titre-hero">
        <div class="container py-5">
            <div class="row align-items-center min-vh-100 py-4">

                <div class="col-lg-6 mb-5 mb-lg-0">
                    <div class="d-flex align-items-center gap-3 mb-4">
                        <div class="bg-white rounded-3 p-2" style="width:52px;height:52px;">
                            <svg viewBox="0 0 24 24" fill="none"
                                 xmlns="http://www.w3.org/2000/svg" width="36" height="36">
                                <rect width="24" height="24" rx="6" fill="#0d6efd"/>
                                <path d="M7 17l4-4 3 3 4-5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <div>
                            <h1 class="h4 fw-bold mb-0" id="titre-hero">SwingApp CRM</h1>
                            <p class="mb-0 opacity-75 small">Gestion clients &amp; prospects</p>
                        </div>
                    </div>

                    <h2 class="display-6 fw-bold mb-3">
                        Pilotez votre portefeuille<br class="d-none d-md-block" />
                        commercial simplement.
                    </h2>
                    <p class="lead opacity-90 mb-4">
                        Une application responsive pour gérer vos clients et prospects,
                        visualiser leurs localisations et suivre la météo de leurs villes.
                    </p>

                    <ul class="list-unstyled row g-3 mb-4">
                        <li class="col-12 col-sm-6"><div class="d-flex align-items-center gap-2"><span class="fs-5">👥</span><span class="small">Gestion clients &amp; prospects</span></div></li>
                        <li class="col-12 col-sm-6"><div class="d-flex align-items-center gap-2"><span class="fs-5">🗺️</span><span class="small">Carte interactive Leaflet OSM</span></div></li>
                        <li class="col-12 col-sm-6"><div class="d-flex align-items-center gap-2"><span class="fs-5">🌤️</span><span class="small">Météo en temps réel</span></div></li>
                        <li class="col-12 col-sm-6"><div class="d-flex align-items-center gap-2"><span class="fs-5">🔒</span><span class="small">Conforme RGPD / RGAA</span></div></li>
                    </ul>

                    <div class="d-lg-none">
                        <button type="button" id="btn-connexion-hero" class="btn btn-light btn-lg fw-bold px-5 shadow" data-bs-toggle="modal" data-bs-target="#modal-connexion">
                            🔑 Se connecter
                        </button>
                    </div>
                </div>

                <div class="col-lg-5 offset-lg-1 d-none d-lg-block">
                    <div class="card shadow-lg border-0 rounded-4 p-4">
                        <div class="card-body">
                            <h3 class="h5 fw-bold text-center text-dark mb-4">
                                Accéder à mon espace
                            </h3>

                            <c:if test="${not empty erreurMessage}">
                                <div class="alert alert-danger text-center small py-2" role="alert">
                                        ${erreurMessage}
                                </div>
                            </c:if>

                            <form id="form-connexion-hero" action="${pageContext.request.contextPath}/app" method="POST" novalidate aria-label="Formulaire de connexion">

                                <input type="hidden" name="cmd" value="login" />

                                <div class="mb-3">
                                    <label for="hero-identifiant" class="form-label text-dark">Identifiant <span class="text-danger" aria-hidden="true">*</span></label>
                                    <input type="text" id="hero-identifiant" name="identifiant" class="form-control" placeholder="Votre identifiant" required autocomplete="username" />
                                </div>

                                <div class="mb-3">
                                    <label for="hero-mdp" class="form-label text-dark">Mot de passe <span class="text-danger" aria-hidden="true">*</span></label>
                                    <div class="input-group">
                                        <input type="password" id="hero-mdp" name="motDePasse" class="form-control" placeholder="Votre mot de passe" required autocomplete="current-password" />
                                        <button type="button" class="btn btn-outline-secondary" id="btn-toggle-hero-mdp">👁</button>
                                    </div>
                                </div>

                                <div class="d-grid mt-4">
                                    <button type="submit" class="btn btn-primary btn-lg fw-bold">Se connecter</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </section>

</main>

<div class="modal fade" id="modal-connexion" tabindex="-1" aria-labelledby="modal-connexion-titre" aria-modal="true" role="dialog">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="modal-title h5" id="modal-connexion-titre">Connexion</h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer la fenêtre"></button>
            </div>
            <div class="modal-body">

                <c:if test="${not empty erreurMessage}">
                    <div class="alert alert-danger text-center small py-2" role="alert">
                            ${erreurMessage}
                    </div>
                </c:if>

                <form id="form-connexion-modal" action="${pageContext.request.contextPath}/app" method="POST" novalidate aria-label="Formulaire de connexion">

                    <input type="hidden" name="cmd" value="login" />

                    <div class="mb-3">
                        <label for="modal-identifiant" class="form-label">Identifiant</label>
                        <input type="text" id="modal-identifiant" name="identifiant" class="form-control" required />
                    </div>
                    <div class="mb-3">
                        <label for="modal-mdp" class="form-label">Mot de passe</label>
                        <input type="password" id="modal-mdp" name="motDePasse" class="form-control" required />
                    </div>

                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-primary">Se connecter</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/rgpd-consent.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/login.js"></script>
</body>
</html>