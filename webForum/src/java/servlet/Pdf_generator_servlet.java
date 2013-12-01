/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import db.DBManager;
import db.Gruppo;
import db.Utente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class Pdf_generator_servlet extends HttpServlet {

    private DBManager manager;
    Gruppo gruppo;
    int numpost = -1;
    Date datalastpost;
    ArrayList<Utente> utenti_gruppo = new ArrayList<Utente>();

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
            numpost = manager.getNumPostPerGruppo(Integer.parseInt(request.getParameter("groupID")));
            datalastpost = manager.getDataUltimoPost(Integer.parseInt(request.getParameter("groupID")));
        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            List<Integer> users_ids = manager.getUtenti(gruppo.getIdgruppo());
            for (Integer user_id : users_ids) {
                Utente u = new Utente();
                u = manager.getMoreUtente(user_id);
                utenti_gruppo.add(u);
            }
        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
        } 

        response.setContentType("application/pdf");

        Document document = new Document();

        try {

            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            if (gruppo != null) {
                document.add(new Paragraph("Nome del gruppo: " + gruppo.getNome()));
                document.add(new Paragraph("Proprietario gruppo: " + gruppo.getOwnerName()));
            } else {
                document.add(new Paragraph("gruppo era null"));
            }
            document.add(new Paragraph("#######"));
            PdfPTable table = new PdfPTable(1); // Code 1

            // Code 2
            table.addCell("Nomi degli utenti partecipanti");
            
             for (Utente utente : utenti_gruppo) {
                table.addCell(utente.getUserName());
            }
             if (utenti_gruppo.isEmpty()) {
                 table.addCell("Il proprietario del gruppo è attualmente l'unico partecipante");
             }
           
            document.add(table);
            if (datalastpost == null || numpost == -1) {
                document.add(new Paragraph("something null"));
            } else {
                document.add(new Paragraph("Data ultimo post: " + datalastpost.toString()));
                document.add(new Paragraph("Numero di post inseriti in questo gruppo: " + numpost));
            }

        } catch (DocumentException de) {
            de.printStackTrace();
            System.err.println("document: " + de.getMessage());
        }

        document.close();
        utenti_gruppo.clear();

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
