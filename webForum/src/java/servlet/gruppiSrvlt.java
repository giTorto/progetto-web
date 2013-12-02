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
public class gruppiSrvlt extends HttpServlet {

    private DBManager manager;
    List<Gruppo> gruppiProp;
    List<Gruppo> gruppiParte;

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
        //response.sendRedirect(request.getContextPath() + "/dist/gruppipage.html");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Utente user = (Utente) session.getAttribute("user");

        try {
            gruppiProp = manager.getGruppiOwner(user);
            gruppiParte = manager.getGruppiPart(user);

        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
         * TODO output your page here. You may use following sample code.
         */
        out.println("    <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        out.println("    <html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("");
        out.println("        <head>");
        out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">");
        out.println("                <script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
        out.println("                <script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
        out.println("                <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />");
        out.println("                <title>Senza nome 1</title>");
        out.println("        </head>");
        out.println("");
        out.println("        <body>");
        out.println("<div class=\"panel panel-default\">");
            out.println("  <div class=\"panel-body\" align=\"right\">");
            out.println("<button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/main" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Home</button>");
            out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Logout</button>");
            out.println("");
            out.println("  </div>");
            out.println("</div>");
            out.println("");
        out.println("");
        out.println("            <div class=\"panel panel-default\">");
        out.println("                <!-- Default panel contents -->");
        out.println("                <div class=\"panel-heading\">");
        out.println("                    <h1 align=\"center\">I miei gruppi</h1></div>");
        out.println("                <div class=\"panel-body\">");
      
        out.println("                </div>");
        out.println("                <!-- Table -->");
        out.println("                <table class=\"table\">");
        out.println("                    <thead>");
        out.println("                        <tr>");
        out.println("<h2>Gruppi di cui sei proprietario</h2>");
        out.println("                            <th>Gruppo</th>");
        out.println("                            <th>Data creazione</th>");
        out.println("                            <th>Link</th>");
        out.println("");
        out.println("                            <th>Modifica</th>");
        out.println("                            <th>PDF</th>");
        out.println("                        </tr>");
        out.println("                    </thead>");

        
        for (Gruppo gruppo : gruppiProp) {
            gruppo.tohtmlrow(out, user);
        }
        out.println("                </table>");
        out.println("                <table class=\"table\">");
        out.println(" <h2>Gruppi a cui partecipi </h2> ");
        out.println("<thead>\n" +
"			<tr>\n" +
"				<th>Gruppo</th>\n" +
"				<th>Data creazione</th>\n" +
"				<th>Link</th>\n" +
"				\n" +
"			</tr>\n" +
"		</thead>");
        for (Gruppo gruppo : gruppiParte) {
            gruppo.tohtmlrow(out, user);
        }

        out.println("                </table>");
        out.println("            </div>");
        out.println("");
        out.println("        </body>");
        out.println("");
        out.println("    </html>");

        out.close();

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
