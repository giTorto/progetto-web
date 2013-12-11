/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
import db.Gruppo;
import db.Utente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myutil.MyUtil;

/**
 *
 * @author Lorenzo
 */
public class SalvaNuovoGruppo extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inviti2parse = "";
        String creazione_gruppoNome = "";
        ArrayList<String> utentiSbagliati = new ArrayList<String>();
        boolean ok_crea_gruppo = true;
        boolean ok_inviti = true;

        /*
         * se arriva una creazione gruppo
         */
        try {
            creazione_gruppoNome = request.getParameter("creazione_gruppo_nome");
            if (creazione_gruppoNome == null) {
                ok_crea_gruppo = false;
            }
        } catch (Exception e) {
            ok_crea_gruppo = false;
        }

        try {
            inviti2parse = request.getParameter("areainviti");
            if (inviti2parse == null) {
                ok_inviti = false;
            }
        } catch (Exception e) {
            ok_inviti = false;
        }

        if (ok_crea_gruppo && !"".equals(inviti2parse) && !"".equals(creazione_gruppoNome) && creazione_gruppoNome != null) {
            try {
                Utente ownernewgroup = (Utente) ((HttpServletRequest) request).getSession().getAttribute("user");
                try {
                    manager.creaGruppo(ownernewgroup, creazione_gruppoNome);

                    Gruppo gruppo_appena_creato = manager.getGruppo(creazione_gruppoNome);
                    ArrayList<String> username_invitati = MyUtil.parseFromString(inviti2parse);
                    utentiSbagliati = MyUtil.sendinviti(username_invitati, gruppo_appena_creato.getIdgruppo(), manager);
                    if (!utentiSbagliati.isEmpty()) {
                        response.setContentType("text/html;charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        try {

                            out.println("<!DOCTYPE html>");
                            out.println("<html>");
                            out.println("<head>");
                            out.println("<title>Servlet loginSrvlt</title>");
                            out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">");
                            out.println("<script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
                            out.println("<script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
                            out.println("</head>");
                            out.println("<body>");
                            out.println("");
                            out.println("  <div class=\"panel-body\" align=\"right\">");

                            out.println("<a href=\"" + request.getContextPath() + "/logg/creaGruppoSrvlt" + "\" style=\"background-color:#cbd5dd\" class=\"btn btn-default\"><span class=\"glyphicon glyphicon-arrow-left\"></span> Indietro</a>");

                            out.println("<button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/main" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Home</button>");
                            out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Logout</button>");
                            out.println("");
                            out.println("  </div>");
                            out.println("</div>");
                            out.println("");
                            out.println("<h1>Gruppo creato correttamente</h1>");
                            out.println("<h2>E' stato impossibile invitare: ");
                            out.println("<ul> ");
                            
                            for (String username : utentiSbagliati) {
                                out.println("<li>" + username + "</li>");
                            }
                            out.println("</ul>");
                
                            out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/gruppiSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Vai ai tuoi gruppi</button>");
                            out.println("</body>");
                            out.println("</html>");

                        } finally {
                            out.close();
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Exception e) {
                System.err.println("Servlet salvanuovogruppo: errore " + e.getMessage());
            }
        }

        ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/logg/gruppiSrvlt");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
