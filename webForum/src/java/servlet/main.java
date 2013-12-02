/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.Utente;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Giulian
 */
public class main extends HttpServlet {

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
        HttpSession sessione = request.getSession();
        Utente u = (Utente) sessione.getAttribute("user");
        String header;

        header = (String) sessione.getAttribute("LastAccess");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();


        /*
         * end logic of cookies
         */
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet loginSrvlt</title>");
            out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">");
            out.println("                <script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
            out.println("                <script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>Ultimo accesso eseguito alle: " + header + "</h1>");

            out.println("<h2> Benvenuto " + ((Utente) request.getSession().getAttribute("user")).getUserName() + " </h2>");

            out.println("<div class=\"panel panel-default\">");
            out.println("  <div class=\"panel-body\" align=\"right\">");

            out.println("<ul class=\"list-group\">\n"
                    + "  <li align=\"center\" class= \"list-group-item\" style=\"background-color:lightgreen; text-color:white\" onclick=\"location.href='" + request.getContextPath() + "/logg/invitiSrvlt" + "'\"><h3>Inviti</h3></li>\n"
                    + "  <li align=\"center\" class=\"list-group-item\" style=\"background-color:lightblue\" onclick=\"location.href='" + request.getContextPath() + "/logg/gruppiSrvlt" + "'\"><h3>Gruppi</h3></li>\n"
                    + "  <li align=\"center\" class=\"list-group-item\" style=\"background-color:orange\" onclick=\"location.href='" + request.getContextPath() + "/logg/creaGruppoSrvlt" + "'\"><h3>Crea gruppo</h3></li>\n"
                    + "  <li align=\"center\" class=\"list-group-item\" style=\"background-color:#CC0000\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\"><h3>Logout</h3></li>\n"
                  
                    + "</ul>");
            out.println("  </div>");
            out.println("</div>");
            out.println("");
//            out.println("<form method=\'post\' action=\'invitiSrvlt\' >");
//            out.println("<input name=\"inviti\" type=\"submit\" value=\"Inviti\"> ");
//            out.println("</form>");
//            out.println("<form method=\'post\' action=\'gruppiSrvlt\' >");
//            out.println("<input name=\"gruppi\" type=\"submit\" value=\"Gruppi\"> ");
//            out.println("</form>");
//            out.println("<form method=\'post\' action=\'creaGruppoSrvlt\' >");
//            out.println("<input name=\"creagruppo\" type=\"submit\" value=\"Crea Gruppo\"> ");
//            out.println("</form>");
//            out.println("<form method=\'post\' action=\'logoutSrvlt\' >");
//            out.println("<input name=\"logout\" type=\"submit\" value=\"LogOut\">");
//            out.println("</form>");

            out.write("</body>");
            out.write("</html>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
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
