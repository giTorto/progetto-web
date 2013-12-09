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
import java.util.ArrayList;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import myutil.MyUtil;

/**
 *
 * @author Giulian
 */
public class aggiungiPost extends HttpServlet {

    private DBManager manager;
    MessageDigest messageDigest = null;
    final String dollars = "$$";

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

        // response.sendRedirect("");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
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
        int ingruppo;
        Integer idgruppo = 0;
        String messaggio = "";
        try {
            ingruppo = Integer.parseInt(request.getParameter("idgruppo"));
        } catch (Exception e) {
            System.err.println("Fileupload: errore con idgruppo");
            ingruppo = -1;
        }

        if (ingruppo != -1) {
            idgruppo = ingruppo;
        }

        try {
            messaggio = (request.getParameter("messaggio"));
        } catch (Exception e) {
            System.err.println("Fileupload: errore con messaggio inviato");
            messaggio = "";
        }

        String fileName, relPath;
        String path;
        String tmp = null;
        fileName = null;
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
                    if (is.available() > 0) {
                        BufferedOutputStream output = null;
                        try {
                            ServletContext scx = getServletContext();
                            path = scx.getRealPath("") + "\\media";
                            relPath = "media";
                            makeDir(path);
                            path += "\\" + idgruppo;
                            makeDir(path);
                            fileName = MyUtil.formatName(item.getName());
                            //seed è il seme per l'algoritmo di hashing, per renderlo unico è composto dal nome del file, l'id utente e il tempo preciso al millisecondo (che garantisce)

                            String seed = fileName + user.getId() + new Timestamp(new java.util.Date().getTime()).toString();
                            tmp = md5(seed);
                            tmp = tmp + MyUtil.getExtension(fileName);
                            path += "\\" + tmp;
                            relPath += "\\" + (idgruppo + "\\" + tmp);

                            output = new BufferedOutputStream(new FileOutputStream(path, false));
                            int data = -1;
                            while ((data = is.read()) != -1) {
                                output.write(data);
                            }

                        } catch (IOException ioe) {
                            throw new ServletException(ioe.getMessage());

                        } finally {
                            is.close();
                            if (output != null) {
                                output.close();
                            }
                        }
                    }
                } else {

                    if ((item.getFieldName()).equals("idgruppo")) {
                        idgruppo = Integer.parseInt(Streams.asString(item.openStream()));
                    } else if ((item.getFieldName()).equals("messaggio")) {
                        messaggio = Streams.asString(item.openStream());
                    }
                }
            }
            String resultament = checkText(messaggio, fileName, tmp, idgruppo);
            manager.addPostFile(user, idgruppo, fileName, tmp, resultament);

        } catch (FileUploadException ex) {
            Logger.getLogger(aggiungiPost.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(aggiungiPost.class.getName()).log(Level.SEVERE, null, ex);
        }

        response.sendRedirect("/webForum/logg/displaygroup?groupID=" + idgruppo);
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

    public void makeDir(String path) throws ServletException {
        File theDir = new File(path);
        if (!theDir.exists()) {
            boolean result = theDir.mkdir();
            if (!result) {
                throw new ServletException("Cannot create a DIR");
            }
        }

    }

    public String md5(String gen) {
        if (messageDigest == null) {
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
     * Controlla il testo sistemando eventuali link
     *
     * @param text testo dell'utente
     * @param fileName eventuale file caricato dall'utente
     * @param fileId eventuale id del file caricato dall'utente
     * @return il testo con i link esatti
     */
    public String checkText(String text, String fileName, String fileId, int idgruppo) {
        Boolean found = false;
        ArrayList<String> parts = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        int end, finder;
        int index = 0;
        String retVal = "", toAdd, nameFound, textFound;
        if (text != null && !text.equals("")) {
            while (!text.equals("")) {
                end = text.indexOf(dollars);

                if (end < 0) {
                    parts.add(index, text);
                    names.add(index, null);
                    text = "";
                } else {
                    toAdd = text.substring(0, end);
                    if (end > 0) {
                        if (found) {
                            finder = toAdd.indexOf(":");
                            if (finder > 0) {
                                nameFound = text.substring(0, finder);
                                textFound = text.substring(finder + 1, end);
                                parts.add(index, textFound);
                                names.add(index, nameFound);
                            } else {
                                parts.add(index, toAdd);
                                names.add(index, "");
                            }
                        } else {
                            parts.add(index, toAdd);
                            names.add(index, null);
                        }
                        index++;

                    }
                    found = !found;
                    text = text.substring(end + 2);
                }

            }
            for (int i = 0; i < parts.size(); i++) {
                nameFound = names.get(i);
                textFound = parts.get(i);
                if (null != nameFound) {

                    String tmp = createLink(textFound, nameFound, fileId, idgruppo);
                    retVal += tmp;

                } else {
                    retVal += textFound;
                }
            }

        }

        return retVal;
    }

    /**
     * Trasforma un link dell'utente in un link HTML
     *
     * @param text testo del link
     * @param name nome dell'utente uploader del file
     * @param id dell'eventuale file caricato dall'utente
     * @return anchor tag aggiustata oppure il testo in input
     */
    public String createLink(String text, String name, String id, int idgruppo) {
        int idt = 0;

        if (!(id == null)) {
            idt = Integer.parseInt(id);
        }

        int tmp;
        if ("-1".equals(id) || id == null) {
            if (!(name.equals("") || name.equals(" "))) {
                if (name.contains("http")) {
                    tmp = 0;
                } else {
                    tmp = manager.getLinkByName(text, name, idgruppo);
                }

            } else {
                tmp = manager.getLRULink(text, idgruppo);
            }
        } else {
            tmp = idt;
        }

        if ("".equals(tmp) || tmp == 0) {
            text = text.toLowerCase();
            if ((name.contains("http") && text.contains("//")) || (name.contains("//") && name.contains("https"))) {
                if (text.contains("www")) {
                    return "<a href='" + name + "://" + text + "'>" + text.substring(2) + "</a>";
                } else {
                    return "<a href='" + name + "://" + text + "'>" + text.substring(2) + "</a>";
                }

            } else if (text.contains("www")) {
                return "<a href='http://" + text + "'>" + text + "</a>";

            } else {
                return text;
            }
        } else {

            return "<a href='fileDownload?fileId=" + tmp + "'>" + text + "</a>";
        }
    }

}
