/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import db.DBManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author matteo.bridarolli
 */
public class FileDownload1 extends HttpServlet {
DBManager manager=null;

  @Override
    public void init() {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

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
        Logger.getLogger(FileDownload1.class.getName()).log(Level.SEVERE, null, ex);
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
        response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
        //scambiare riga sopra con la seconda riga sotto per download 
        //diretto invece di provarlo ad aprire nel browser
        //response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
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
    static public void showError() throws FileNotFoundException{
        //scegli cosa fare, o reindirizzi o mandi l'errore (oppure reindirizzi ad una pagina con un messaggio d'errore, che crei tu, tipo 404)
        throw new FileNotFoundException("You do not have priviliegies or the file is missing");
        //response.sendRedirect(request.getContextPath() + "/index.html"); 

    }
}
