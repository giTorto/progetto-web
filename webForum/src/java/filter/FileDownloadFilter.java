/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import db.DBManager;
import db.Gruppo;
import db.Utente;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import servlet.gruppiSrvlt;

/**
 *
 * @author Lorenzo
 */
public class FileDownloadFilter implements Filter {

    private DBManager manager;
    private static final boolean debug = true;
    Utente user;
    int idgruppo = -1;
    String fileId;
    boolean permesso_accesso_file = false;
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public FileDownloadFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        permesso_accesso_file = false;
        boolean something_wrong = false; //ad esempio se uno chiama un file in get e non è loggato
        idgruppo = -1;
        if (debug) {
            log("FileDownloadFilter:doFilter()");
        }
        //recuperare l'idgruppo relativo al fileid
        try {
            fileId = request.getParameter("fileId");
        } catch (Exception e) {
            System.err.println("Filtro accesso ai file: errore nel recupero del fileid");
            something_wrong = true;
        }

        try {
            idgruppo = manager.getGroupid_from_Fileid(Integer.parseInt(fileId));
        } catch (SQLException e) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, e);
            ((HttpServletResponse) response).sendRedirect("errorpage.html");
        }

        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            user = (Utente) session.getAttribute("user");
            if (user == null) {
                something_wrong = true;
            }
        }

        try {
            if (user != null) {
                ArrayList<Integer> ListidsPartecipanti = (ArrayList<Integer>) manager.getUtenti(idgruppo);
                Gruppo gruppo = manager.getGruppo(idgruppo);
                Utente owner = manager.getMoreByUserName(gruppo.getOwnerName());
                ListidsPartecipanti.add(owner.getId());
                if (ListidsPartecipanti.contains(user.getId())) {
                    permesso_accesso_file = true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
            ((HttpServletResponse) response).sendRedirect("errorpage.html");
        }

        if (!permesso_accesso_file || something_wrong) {

            ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/logg/gruppiSrvlt ");
        } else {
            chain.doFilter(request, response);
        }

    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("FileDownloadFilter:Initializing filter");
            }
        }

        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) filterConfig.getServletContext().getAttribute("dbmanager");
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("CheckAccessUser2Group()");
        }
        StringBuffer sb = new StringBuffer("CheckAccessUser2Group(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
