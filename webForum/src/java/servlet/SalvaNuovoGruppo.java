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
                    MyUtil.sendinviti(username_invitati, gruppo_appena_creato.getIdgruppo(), manager);
                } catch (SQLException ex) {
                    Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Exception e) {
                System.err.println("Servlet salvanuovogruppo: errore "+e.getMessage());
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
