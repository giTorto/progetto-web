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

        if (ok_inviti && !"".equals(inviti2parse) && inviti2parse != null && groupid != -1) {
            try {
                ArrayList<String> username_invitati = MyUtil.parseFromString(inviti2parse);
                MyUtil.sendinviti(username_invitati, groupid, manager);
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
