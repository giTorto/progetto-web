/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Giulian
 */
public class FileDownload extends HttpServlet {
DBManager manager=null;

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
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FileDownload</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet FileDownload at " + request.getContextPath() + "</h1>");
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
          String fileId = request.getParameter("fileId").toString();
        String groupId = "2";
        if(fileId==null){
         showError();
        }
        HashMap<String, String> dbData = null;
    try {
        dbData = manager.getRealAndDBName(Integer.parseInt(fileId)); //qui va fatto un filtro sui idpost,idgruppo e idutente
    } catch (SQLException ex) {
        Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
    }
        String path=dbData.get("path");
        String name=dbData.get("name");
        ServletContext ctx = getServletContext();
        File download=new File(ctx.getRealPath("")+"\\"+path);
        if(name==null){
        showError();
        }else{
        InputStream fis = new FileInputStream(download);
        String mimeType = ctx.getMimeType(download.getAbsolutePath());
	response.setContentType(mimeType != null? mimeType:"application/octet-stream");
	response.setContentLength((int) download.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        ServletOutputStream os= response.getOutputStream();
        byte[] bufferData = new byte[1024];
        int read=0;
        while((read = fis.read(bufferData))!= -1){
            os.write(bufferData, 0, read);
		}
        os.flush();
        os.close();
        fis.close();}
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
    
      static public void showError() throws FileNotFoundException{
        //scegli cosa fare, o reindirizzi o mandi l'errore (oppure reindirizzi ad una pagina con un messaggio d'errore, che crei tu, tipo 404)
        throw new FileNotFoundException("You do not have priviliegies or the file is missing");
        //response.sendRedirect(request.getContextPath() + "/index.html"); 

    }

}
