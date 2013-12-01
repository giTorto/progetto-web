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

        int idgruppo = Integer.parseInt(request.getParameter("groupID"));
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
            out.println("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
            out.println("<title>Senza nome 1</title>");
            out.println("<style type=\"text/css\">");
            out.println("</style>");
            out.println("</head>");
            out.println("");
            out.println("<body>");

            out.println("<div>");
            out.println("");
            out.println("<h1>" + gruppo.getNome() + "</h1><br>");
            //out.write("<form action=\"" + ((HttpServletRequest) request).getRequestURI() + "/gestionefile\" method=\"post\" enctype=\"multipart/form-data\">");
            out.write("<form action=\"aggiungiPost\" method =\"post\"  enctype=\"multipart/form-data\" >");
            out.write("Select File to Upload:<input type=\"file\" name=\"file\">");
            out.write("<br>");
            //out.write("<input type=\"submit\" value=\"Upload\">");
            //out.write("</form>");

            out.println("	</h1>");

            out.println("	<p></p>");
           // out.println("	<form action=\"" + ((HttpServletRequest) request).getRequestURI() + "\" method=\"post\">");
            out.println("	<input name=\"groupID\" type=\"hidden\" value=\"" + gruppo.getIdgruppo() + "\" />	"
                    + "<textarea id=\"txtarea\" cols=\"50\" name=\"messaggio\" rows=\"4\" wrap=\"soft\">scrivi quello che vuoi</textarea><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + "<input name=\"Submit1\" type=\"submit\" value=\"send\" /></form>");
            out.println("	<p></p>");
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
