package com.julio.controller;

import com.julio.controller.client.CreateClientCommand;
import com.julio.controller.client.DeleteClientCommand;
import com.julio.controller.client.EditClientCommand;
import com.julio.controller.client.ListClientsCommand;
import com.julio.controller.client.SaveClientCommand;
import com.julio.controller.client.ViewClientCommand;
import com.julio.controller.common.AccueilCommand;
import com.julio.controller.common.LoginCommand;
import com.julio.controller.common.LogoutCommand;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Design Pattern : Factory (Fabrique).
 * Centralise l'instanciation et la distribution des commandes de l'application.
 * Respecte le principe de Responsabilité Unique (SRP) pour le FrontController.
 */
@Slf4j
public class CommandFactory {
  // Registre statique contenant toutes les commandes prêtes à l'emploi
  private static final Map<String, Icommand> commands = new HashMap<>();

  // Le bloc 'static' est exécuté une seule fois au démarrage de l'application
  static {
    log.info("⚙️ Initialisation de la CommandFactory...");

    // --- Commandes Communes ---
    // --- Commandes Communes ---
    commands.put("login", new LoginCommand());
    commands.put("logout", new LogoutCommand());
    commands.put("accueil", new AccueilCommand());

    // --- Commandes Clients ---
    commands.put("listClients", new ListClientsCommand());
    commands.put("createClient", new CreateClientCommand());
    commands.put("saveClient", new SaveClientCommand());
    commands.put("editClient", new EditClientCommand());
    commands.put("viewClient", new ViewClientCommand());
    commands.put("deleteClient", new DeleteClientCommand());
  }

  /**
   * Retourne la commande correspondant à l'action demandée.
   *
   * @param commandName La valeur du paramètre 'cmd' (ex : "listClients")
   * @return L'instance de Icommand, ou la commande par défaut si non trouvée
   */
  public static Icommand getCommand(String commandName) {
    // Sécurité : si aucune action n'est fournie, on renvoie vers l'accueil
    if (commandName == null || commandName.isBlank()) {
      return commands.get("login");
    }

    Icommand command = commands.get(commandName);

    // Si l'utilisateur tape une URL avec une commande qui n'existe pas
    if (command == null) {
      log.warn("⚠️ Commande introuvable pour l'action : {}", commandName);
      // On le renvoie par défaut vers l'accueil
      return commands.get("accueil");
    }

    return command;
  }
}
