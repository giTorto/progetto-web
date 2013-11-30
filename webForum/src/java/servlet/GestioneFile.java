/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Lorenzo
 */
public class GestioneFile extends HttpServlet {

  private static final long serialVersionUID = 1L;
    private ServletFileUpload uploader = null;

    @Override
    public void init() throws ServletException {
        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
        fileFactory.setRepository(filesDir);
        this.uploader = new ServletFileUpload(fileFactory);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.equals("")) {
            throw new ServletException("File Name can't be null or empty");
        }
        File file = new File(request.getServletContext().getAttribute("FILES_DIR") + File.separator + fileName);
        if (!file.exists()) {
            throw new ServletException("File doesn't exists on server.");
        }
        System.out.println("File location on server::" + file.getAbsolutePath());
        ServletContext ctx = getServletContext();
        InputStream fis = new FileInputStream(file);
        String mimeType = ctx.getMimeType(file.getAbsolutePath());
        response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        ServletOutputStream os = response.getOutputStream();
        byte[] bufferData = new byte[1024];
        int read = 0;
        while ((read = fis.read(bufferData)) != -1) {
            os.write(bufferData, 0, read);
        }
        os.flush();
        os.close();
        fis.close();
        System.out.println("File downloaded at client successfully");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException("Content type is not multipart/form-data");
        }
        List<FileItem> fileItemsList = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write("<html><head></head><body>");
        out.println("<p><h1>Gestione files</h1></p>");
        out.write("<form action=\"" + ((HttpServletRequest) request).getRequestURI() + "\" method=\"post\" enctype=\"multipart/form-data\">");
        out.write("Select File to Upload:<input type=\"file\" name=\"fileName1\">");
        out.write("<br>");
        out.write("Select File to Upload:<input type=\"file\" name=\"fileName2\">");
        out.write("<br>");
        out.write("Select File to Upload:<input type=\"file\" name=\"fileName3\">");
        out.write("<br>");
        out.write("<input type=\"submit\" value=\"Upload\">");
        out.write("</form>");
         /*
         *
         */
        File folder = new File(request.getServletContext().getAttribute("FILES_DIR") + File.separator);
        File[] listOfFiles = folder.listFiles();

        String filesname = "placeholder";

        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                filesname = listOfFiles[i].getName();
                out.write("<a href=\"gestione file?fileName=" + filesname + "\">Download " + filesname + "</a><br><br>");

            }
        }
        /*
         *
         */
        try {
            fileItemsList = uploader.parseRequest(request);
            Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
            while (fileItemsIterator.hasNext()) {
                FileItem fileItem = fileItemsIterator.next();
                System.out.println("FieldName=" + fileItem.getFieldName());
                System.out.println("FileName=" + fileItem.getName());
                System.out.println("ContentType=" + fileItem.getContentType());
                System.out.println("Size in bytes=" + fileItem.getSize());

                File file = new File(request.getServletContext().getAttribute("FILES_DIR") + File.separator + fileItem.getName());
                System.out.println("Absolute Path at server=" + file.getAbsolutePath());
                fileItem.write(file);
                out.write("File " + fileItem.getName() + " uploaded successfully.");
                out.write("<br>");
                out.write("<a href=\"gestionefile?fileName=" + fileItem.getName() + "\">Download " + fileItem.getName() + "</a><br><br>");
            }

        } catch (FileUploadException e) {
            if (fileItemsList.isEmpty()) {
                out.write("Nessun nuovo upload");
            } else {
                out.write("Exception in uploading file.");
            }
        } catch (Exception e) {

            out.write("");
        }

       
        out.write("</body></html>");
    }

}