package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande listant tous les clients.
 * Récupère la liste via ClientDAO et l'envoie à la JSP.
 */
@Slf4j
public class ListClientsCommand implements Icommand {
  // private final ClientDao clientDao = new ClientDao();

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // ✅ DEBUG : début de l'exécution, utile pour tracer les flux en dev
    log.debug("Exécution de ListClientsCommand — récupération de la liste clients");

    ClientDao clientDao = new ClientDao();
    List<Client> clients = clientDao.findAll();
    request.setAttribute("clients", clients);
    request.setAttribute("bbClients", clients.size());

    // ✅ INFO : résultat métier significatif (paramétrisé, pas de concaténation)
    log.info("ListClientsCommand — {} client(s) chargé(s) depuis la base", clients.size());

    return "/WEB-INF/views/client/list-clients.jsp";
  }
}
