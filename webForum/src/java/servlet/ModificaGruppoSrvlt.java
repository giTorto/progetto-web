/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
import db.Gruppo;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lorenzo
 */
public class ModificaGruppoSrvlt extends HttpServlet {

    private DBManager manager;
    Gruppo gruppo;

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

        try {
            gruppo = manager.getGruppo(Integer.parseInt(request.getParameter("groupID")));

        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
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
            out.println("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />");
            out.println("<title>Senza nome 1</title>");
            out.println("<link href=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css\" rel=\"stylesheet\">");
            out.println("<script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
            out.println("<script src=\"//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js\"></script>");
            out.println("</head>");
            out.println("");
            out.println("<body>");
            out.println("");
            out.println("<h2>Nome gruppo:</h2>");
            out.println("			<form action=\"" + request.getContextPath() + "/logg/gruppiSrvlt\" method=\"post\" name=\"\">");
            out.println("<div class=\"row\">");
            out.println("	<div class=\"col-lg-6\">");
            out.println("		<div class=\"input-group\">");
            out.println("				<input name=\"groupID\" type=\"hidden\" value=\"" + gruppo.getIdgruppo() + "\" />");
            out.println("				<input class=\"form-control\" name=\"nuovo_nome_gruppo\" type=\"text\" value=\"" + gruppo.getNome() + "\">");
            out.println("				<span class=\"input-group-btn\">");
            out.println("				<button class=\"btn btn-default\" type=\"submit\">Salva modifche");
            out.println("				</button></span>");

            out.println("		</div>");
            out.println("		<!-- /input-group --></div>");
            out.println("	<!-- /.col-lg-6 --></div>");
            out.println("<!-- /.row -->");
            out.println("			</form><br><br>");
            out.println("<h2>Inviti:</h2>");
            out.println("<p>Inserisci gli username degli utenti che vuoi invitare separati da una virgola</p>");

            out.println("			<form action=\"\" method=\"post\" name=\"\">");
            out.println("<div class=\"row\">");
            out.println("	<div class=\"col-lg-6\">");
            out.println("		<div class=\"input-group\">");
            out.println("				<input name=\"groupID\" type=\"hidden\" value=\"" + gruppo.getIdgruppo() + "\" />");
            out.println("				<input class=\"form-control\" name=\"areainviti\" size=\"50\" type=\"text\" value=\"ad es: username1,username2\">");
            out.println("				<span class=\"input-group-btn\">");
            out.println("				<button class=\"btn btn-default\" type=\"submit\">Salva modifche");
            out.println("				</button>");

            out.println("			</span></div>");
            out.println("		<!-- /input-group --></div>");
            out.println("	<!-- /.col-lg-6 --></div>");
            out.println("<!-- /.row -->");
            out.println("			</form>");
            out.println("");
            out.println("</body>");
            out.println("");
            out.println("</html>");
            out.println("");
            out.println("");
            out.println("");
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
