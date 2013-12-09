/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
import db.Gruppo;
import db.Post;
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
import javax.servlet.http.HttpSession;

/**
 *
 * @author Lorenzo
 */
public class DisplayGroupSrvlt extends HttpServlet {

    private DBManager manager;
    Gruppo gruppo;
    ArrayList<Post> postlist;
    Utente user;
    String nuovopost = "";

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

        String ingruppo = (request.getParameter("groupID"));

        int idgruppo = Integer.parseInt(ingruppo);
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            user = (Utente) session.getAttribute("user");
        }

        nuovopost = request.getParameter("messaggio");
        if (nuovopost != null) {
            try {
                manager.aggiungiPost(user, idgruppo, nuovopost);
            } catch (SQLException ex) {
                Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                ((HttpServletResponse) response).sendRedirect("errorpage.html");
            }
        }

        try {
            gruppo = manager.getGruppo(idgruppo);
            postlist = (ArrayList<Post>) manager.getPostsGruppo(gruppo);

        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
            //((HttpServletResponse)response).sendRedirect("errorpage.html");
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("");
            out.println("<head>");
            out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">");
            out.println("                <script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
            out.println("                <script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
            out.println("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
            out.println("<title>Senza nome 1</title>");
            out.println("<style type=\"text/css\">");
            out.println("</style>");
            out.println("<style>");
            out.println(".bubble \n"
                    + "{\n"
                    + "position: relative;\n"
                    + "width: 250px;\n"
                    + "height: 120px;\n"
                    + "padding: 0px;\n"
                    + "background: #CBD5DD;\n"
                    + "-webkit-border-radius: 10px;\n"
                    + "-moz-border-radius: 10px;\n"
                    + "border-radius: 10px;\n"
                    + "}\n"
                    + "\n"
                    + ".bubble:after \n"
                    + "{\n"
                    + "content: '';\n"
                    + "position: absolute;\n"
                    + "border-style: solid;\n"
                    + "border-width: 9px 19px 9px 0;\n"
                    + "border-color: transparent #CBD5DD;\n"
                    + "display: block;\n"
                    + "width: 0;\n"
                    + "z-index: 1;\n"
                    + "margin-top: -9px;\n"
                    + "left: -19px;\n"
                    + "top: 74%;\n"
                    + "}");
            out.println("</style>");
            out.println("</head>");
            out.println("");
            out.println("<body>");
            out.println("<div class=\"panel panel-default\">");
            out.println("  <div class=\"panel-body\" align=\"right\">");
            out.println("<a href=\"" + request.getContextPath() + "/logg/gruppiSrvlt" + "\" style=\"background-color:#cbd5dd\" class=\"btn btn-default\"><span class=\"glyphicon glyphicon-arrow-left\"></span> Indietro</a>");
            out.println("<button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/main" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Home</button>");
            out.println("  <button style=\"background-color:#cbd5dd\" onclick=\"location.href='" + request.getContextPath() + "/logg/logoutSrvlt" + "'\" type=\"button\" class=\"btn btn-default\" align=\"right\">Logout</button>");
            out.println("");
            out.println("  </div>");
            out.println("</div>");
            out.println("");
            out.println("<div class=\"panel panel-default\" margin-left=\"auto\";\n"
                    + "    margin-right=\"auto\";\n"
                    + "    margin-top=\"0\";\n"
                    + "    margin-bottom=\"0\";\n"
                    + "    padding=\"0\";>");
            out.println("  <div class=\"panel-body\">");
            out.println("<div>");
            out.println("");
            out.println("<h1>" + gruppo.getNome() + "</h1><br>");
            //out.write("<form action=\"" + ((HttpServletRequest) request).getRequestURI() + "/gestionefile\" method=\"post\" enctype=\"multipart/form-data\">");
            out.write("<form action=\"aggiungiPost\" method =\"post\"  enctype=\"multipart/form-data\" >");
            out.println("	<input name=\"idgruppo\" type=\"hidden\" value=\"" + ((Integer) gruppo.getIdgruppo()).toString() + "\" />	"
                    // + "<textarea id=\"txtarea\" cols=\"50\" name=\"messaggio\" rows=\"4\" wrap=\"soft\">scrivi quello che vuoi</textarea><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"

                    + "<textarea class=\"form-control\" name=\"messaggio\" rows=\"3\" cols=\"50\" placeholder=\"Cosa stai pensando?\"></textarea>");
            out.write("Select File to Upload:<input  type=\"file\" name=\"file\">"
                    + "<input class=\"btn btn-primary btn-lg\" name=\"submit\" type=\"submit\" value=\"send\" /></form>");
            out.write("<br>");
            //out.write("<input type=\"submit\" value=\"Upload\">");
            //out.write("</form>");

            out.println("	</h1>");

            out.println("	<p></p>");
            // out.println("	<form action=\"" + ((HttpServletRequest) request).getRequestURI() + "\" method=\"post\">");

            out.println("	<p></p>");
            out.println("</div>");
            out.println("  </div>");
            out.println("</div>");

            out.println("<div>");
            out.println("	<ul>");
            for (Post post : postlist) {
                post.tohtmlrow(out, user);

            }
            out.println("	</ul>");
            out.println("</div>");
            out.println("");
            out.println("</body>");
            out.println("");
            out.println("</html>");
            out.println("");

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
        System.out.println(request.getRequestURI());
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
