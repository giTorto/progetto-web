/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBManager;
import db.Utente;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import org.apache.commons.fileupload.FileItemStream;

/**
 *
 * @author Giulian
 */
public class aggiungiPost extends HttpServlet {
    private DBManager manager;
    MessageDigest messageDigest=null;
     final String dollars="$$";
    
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
        
        response.sendRedirect("");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet aggiungiPost</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet aggiungiPost at " + request.getContextPath() + "</h1>");
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
        int idgruppo = Integer.parseInt(request.getParameter("groupID"));
        String messaggio = (request.getParameter("messaggio"));
        String fileName,relPath;
        fileName = request.getParameter("file");
        InputStream inStream; 
       
        HttpSession sessione = request.getSession();
        Utente user = (Utente) sessione.getAttribute("user");
        
       
         try {
            ServletFileUpload fileUpload = new ServletFileUpload();
            FileItemIterator items = fileUpload.getItemIterator(request);
            
            while (items.hasNext()) {
                FileItemStream item = items.next();
                if (!item.isFormField()) {
                    InputStream is = new BufferedInputStream(item.openStream());
                    if(is.available()>0){
                        BufferedOutputStream output = null;
                        try {
                            ServletContext scx=getServletContext();
                            String path=scx.getRealPath("")+"\\media";
                            relPath = "media";
                            path+="\\"+idgruppo;
                            makeDir(path);
                            fileName=formatName(item.getName());
                            //seed è il seme per l'algoritmo di hashing, per renderlo unico è composto dal nome del file, l'id utente e il tempo preciso al millisecondo (che garantisce)
                            
                            String seed=fileName+user.getId()+new Timestamp(new java.util.Date().getTime()).toString();
                            String tmp=md5(seed);
                            tmp=tmp+getExtension(fileName);
                            path+="\\"+tmp;
                            relPath+="\\"+(idgruppo+"\\"+tmp);
                            manager.addPostFile(user,idgruppo, fileName, tmp,messaggio);
                            output = new BufferedOutputStream(new FileOutputStream(path, false));
                            int data = -1;
                            while ((data = is.read()) != -1) {
                                output.write(data);
                            }
                            
                        } catch(IOException ioe){
                            throw new ServletException(ioe.getMessage());
                        } catch (SQLException ex) {
                            Logger.getLogger(aggiungiPost.class.getName()).log(Level.SEVERE, null, ex);
                        }finally {
                            is.close();
                            if(output!=null){
                                output.close();}
                        }
                    }
                }
            }
        }catch (FileUploadException ex) {
            Logger.getLogger(aggiungiPost.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void makeDir(String path) throws ServletException{
        File theDir = new File(path);
        if (!theDir.exists()) {
            boolean result = theDir.mkdir();
            if(!result) {
                throw new ServletException("Cannot create a DIR");
            }
        }
                 
    }
    
      public String md5(String gen){
        if(messageDigest==null){
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(aggiungiPost.class.getName()).log(Level.SEVERE, "Non va il cypher", ex);
            }
        }
        byte[] mdbytes = messageDigest.digest(gen.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
      
         /**
     *Su alcuni browser (IE) il file viene caricato con il path assoluto, quindi bisogna recuperare il solo nome del file 
     * @param fileName
     * @return vero nome del file
     */
    public String formatName(String fileName){
        int index=fileName.lastIndexOf("\\");
        if(index>0){
            return fileName.substring(index+1);
        }
        return fileName;
    }

    
      /**
     *Dato un file, trova la sua estensione
     * @param fileName nome del file da controllare
     * @return estensione del file in input
     */
    public String getExtension(String fileName){
        int index=fileName.lastIndexOf(".");
        return (index>=0)?fileName.substring(index):"";
    }
}
