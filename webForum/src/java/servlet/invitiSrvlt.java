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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Giulian
 */
public class invitiSrvlt extends HttpServlet {

    private DBManager manager;
    List<Gruppo> inviti;

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

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Utente user = (Utente) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        try {
            /*
             * TODO output your page here. You may use following sample code.
             */

            inviti = manager.getInvitiGruppi(user);

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet invitiSrvlt</title>");
            out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">");
            out.println("                <script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
            out.println("                <script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"panel panel-default\">");
            out.println("  <div class=\"panel-body\" align=\"right\">");
            out.println("<button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/main" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Home</button>");
            out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Logout</button>");
            out.println("");
            out.println("  </div>");
            out.println("</div>");
            out.println("");
            out.println("<h1> Inviti a " + user.getUserName() + "</h1>");
            out.println("<h1>Servlet invitiSrvlt at " + request.getContextPath() + "</h1>");
            out.println("<form action=\"accettaInvitiSrvlt\" method=\"GET\">");
            out.println("<table border=\'1\'> <tr> <th> Owner </th> <th> Nome Gruppo </th> <th>Data creazione</th> <th> Sei stato invitato<br>spunta la casella per accettare l'invito </th> </tr>");

            for (Gruppo gruppo : inviti) {
                out.println("<tr> <td>" + gruppo.getOwnerName() + "</td> <td> " + gruppo.getNome() + "</td><td>"
                        + gruppo.getDataCreazione().toString() + "</td><td><input name=\""
                        + gruppo.getIdgruppo() + "\" type=\"checkbox\" value=\"Accetto\" checked=\"checked\"/></td>");
            }
            out.println("</table> ");
            out.println("<input type=\"submit\" value=\"Fatto\"  > ");
            out.println("</form> </body> </html>");
        } catch (SQLException ex) {
            Logger.getLogger(invitiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
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
