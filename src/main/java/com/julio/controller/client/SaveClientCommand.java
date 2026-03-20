package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Sauvegarde de clients.
 */
@Slf4j
public class SaveClientCommand implements Icommand {
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "";
  }

}
