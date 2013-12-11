/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
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
public class salvaModificheGruppo extends HttpServlet {

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
        boolean puoiandareavanti_nome = true;
        boolean ok_inviti = true;
        int groupid = -1;
        String new_group_name = "";
        String inviti2parse = "";
        try {
            groupid = Integer.parseInt(request.getParameter("groupID"));
        } catch (Exception e) {
            System.err.println("Errore nel recupero dell'groupid");
        }


        /*
         * se cambia il nome da bottone modifica
         */
        try {
            new_group_name = request.getParameter("nuovo_nome_gruppo");
            if (new_group_name == null) {
                puoiandareavanti_nome = false;
            }
        } catch (Exception e) {
            puoiandareavanti_nome = false;
        }

        if (puoiandareavanti_nome && !"".equals(new_group_name) && new_group_name != null) {
            try {
                manager.updateGroupName(groupid, new_group_name);
            } catch (SQLException ex) {
                Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*
         * se manda inviti da bottone modifica
         */
        try {
            inviti2parse = request.getParameter("areainviti");
            if (inviti2parse == null) {
                ok_inviti = false;
            }
        } catch (Exception e) {
            ok_inviti = false;
        }
        ArrayList<String> userSbagliati = new ArrayList<String>();

        if (ok_inviti && !"".equals(inviti2parse) && inviti2parse != null && groupid != -1) {

            try {
                ArrayList<String> username_invitati = MyUtil.parseFromString(inviti2parse);
                userSbagliati = MyUtil.sendinviti(username_invitati, groupid, manager);

                if (!userSbagliati.isEmpty()) {
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

                        out.println("<a href=\"" + request.getContextPath() + "/logg/gruppiSrvlt" + "\" style=\"background-color:#cbd5dd\" class=\"btn btn-default\"><span class=\"glyphicon glyphicon-arrow-left\"></span> Indietro</a>");

                        out.println("<button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/main" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Home</button>");
                        out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Logout</button>");
                        out.println("");
                        out.println("  </div>");
                        out.println("</div>");
                        out.println("");
                        out.println("<h2>E' stato impossibile invitare: ");
                        out.println("<ul>");
                        for (String username : userSbagliati) {
                            out.println("<li>"+username+"</li>");                            
                        }
                        out.println("</ul>");
                         out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/gruppiSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Torna ai gruppi</button>");
                        out.println("</body>");
                        out.println("</html>");

                    } finally {
                        out.close();
                    }
                }

            } catch (Exception e) {
                System.err.println("Errore negli inviti");
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
