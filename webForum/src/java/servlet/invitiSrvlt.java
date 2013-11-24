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
            out.println("</head>");
            out.println("<body>");
            out.println("<h1> Inviti a " + user.getUserName() + "</h1>");
            out.println("<h1>Servlet invitiSrvlt at " + request.getContextPath() + "</h1>");
            out.println("<form>");
            out.println("<table border=\'1\'> <tr> <th> Owner </th> <th> Nome Gruppo </th> <th>Data creazione</th> <th> Accetti </th> </tr>");
            int i = 0;
            for (Gruppo gruppo : inviti){
                out.println("<tr> <td>"+ inviti.get(i).getOwnerName()+ "</td> <td> "+ inviti.get(i).getNome() + "</td><td>"+
                        inviti.get(i).getDataCreazione().toString()+"</td><td><input name=\" "
                        + inviti.get(i).getIdgruppo() +" \" type=\"checkbox\" value=\"Accetto\" checked=\"checked\"/></td>");
            }
            out.println("</table> ");
            out.println("<input type=\"submit\" value=\"Fatto\" action=\"accettaInvitiSrvlt\" > ");
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
