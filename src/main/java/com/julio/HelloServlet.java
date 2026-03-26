package com.julio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet.
 *
 */
public class HelloServlet {
  private String message;

  /**
   * Méthode init.
   *
   */
  public void init() {
    message = "Hello World!";
  }

  /**
   * Méthode doGet.
   *
   * @param request request
   * @param response response
   * @throws IOException exception
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    // Hello
    PrintWriter out = response.getWriter();
    out.println("<html><body>");
    out.println("<h1>" + message + "</h1>");
    out.println("</body></html>");
  }

  /**
   * Methode destroy.
   *
   */
  public void destroy() {
  }
}