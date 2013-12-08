/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
import db.Utente;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Giulian
 */
public class loginSrvlt extends HttpServlet {

    private DBManager manager;
    Utente user;

    @Override
    public void init() {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    public void checkESetCookie(HttpServletRequest request, HttpServletResponse response) {

        String header = "";

        HttpSession sessione = request.getSession();
        Utente u = (Utente) sessione.getAttribute("user");

        Date date = Calendar.getInstance().getTime();

        /*
         * logic of cookies
         */
        Cookie[] mycookies = request.getCookies();
        for (Cookie cookie : mycookies) {
            if (cookie.getName().equals(u.getUserName())) {
                header = cookie.getValue();
                cookie.setMaxAge(0);
            }
        }
        if (header.equals("")) {
            //allora non avevamo il cookike timestamp
            header = "Benvenuto per la prima volta\n";
        }
        Cookie timecookie = new Cookie(u.getUserName(), date.toString());
        timecookie.setMaxAge(730000);
        response.addCookie(timecookie);

        sessione.setAttribute("LastAccess", header);
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
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + header + "</h1>");
            out.println("<h1>Servlet loginSrvlt at " + request.getContextPath() + "</h1>");
            out.println("<h2> Benvenuto" + ((Utente) request.getSession().getAttribute("user")).getUserName() + " </h2>");
            out.println("<form method=\'post\' action=\'invitiSrvlt\' >");
            out.println("<input name=\"inviti\" type=\"submit\" value=\"Inviti\"> ");
            out.println("</form>");
            out.println("<form method=\'post\' action=\'gruppiSrvlt\' >");
            out.println("<input name=\"gruppi\" type=\"submit\" value=\"Gruppi\"> ");
            out.println("</form>");
            out.println("<form method=\'post\' action=\'creaGruppoSrvlt\' >");
            out.println("<input name=\"creagruppo\" type=\"submit\" value=\"Crea Gruppo\"> ");
            out.println("</form>");
            out.println("<form method=\'post\' action=\'logoutSrvlt\' >");
            out.println("<input name=\"logout\" type=\"submit\" value=\"LogOut\">");
            out.println("</form>");
            out.write("<html>");
            out.write("<form action=\"UploadDownloadFileServlet\" method=\"post\" enctype=\"multipart/form-data\">");
            out.write("Select File to Upload:<input type=\"file\" name=\"fileName\">");
            out.write("<br>");
            out.write("<input type=\"submit\" value=\"Upload\">");
            out.write("</form>");
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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // controllo nel DB se esiste un utente con lo stesso username + password

        try {
            user = manager.authenticate(username, password);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        if (user == null) {
            PrintWriter out = response.getWriter();
            String output = "<html>\n"
                    + "    <head>\n"
                    + "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                    + "        <title>JSP Page</title>\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <h2 style=\"color: red\">Login errato, riprova per favore </h2>\n"
                    + "        <h1>Primo progetto: webForum</h1>\n"
                    + "        <form method =\"post\" action=\"loginSrvlt\">\n"
                    + "            <input type=\"text\" name=\"username\">\n"
                    + "            <input type=\"password\" name=\"password\">\n"
                    + "            <input type=\"submit\" value=\"submit\">\n"
                    + "        </form>\n"
                    + "    </body>\n"
                    + "</html>";
            out.println(output);
        } else {
            // imposto l'utente connesso come attributo di sessione
            // per adesso e' solo un oggetto String con il nome dell'utente, ma posso metterci anche un oggetto User
            // con, ad esempio, il timestamp di login

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            checkESetCookie(request, response);

            //processRequest(request, response);
            response.sendRedirect(request.getContextPath() + "/logg/main");

        }

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
