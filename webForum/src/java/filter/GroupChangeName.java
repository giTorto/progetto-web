/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import db.DBManager;

import db.Utente;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import servlet.gruppiSrvlt;
import db.Gruppo;
import servlet.invitiSrvlt;
import util.Util;

/**
 *
 * @author Lorenzo
 */
public class GroupChangeName implements Filter {

    private static final boolean debug = true;
    private DBManager manager;

    private ArrayList<String> parseFromString(String phrase_inviti) {
        ArrayList<String> retval = new ArrayList<String>();
        retval.clear();
        String delims = "[,]";
        String[] tokens = phrase_inviti.split(delims);
        retval.addAll(Arrays.asList(tokens));
        return retval;

    }

    

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public GroupChangeName() {
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
        boolean puoiandareavanti_nome = true;
        boolean ok_inviti = true;
        boolean ok_crea_gruppo = true;
        int groupid = -1;
        String new_group_name = "";
        String inviti2parse = "";
        String creazione_gruppoNome = "";

        
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

        if (ok_inviti && !"".equals(inviti2parse) && inviti2parse != null  && groupid != -1) {
            try {
                ArrayList<String> username_invitati = parseFromString(inviti2parse);
                Util.sendinviti(username_invitati, groupid,manager);
            } catch (Exception e) {
                System.err.println("Errore negli inviti");
            }
        }
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

        if (ok_crea_gruppo && !"".equals(inviti2parse) && inviti2parse != null && !"".equals(creazione_gruppoNome) && creazione_gruppoNome != null) {
            try {
                Utente ownernewgroup = (Utente) ((HttpServletRequest) request).getSession().getAttribute("user");
                try {
                    manager.creaGruppo(ownernewgroup, creazione_gruppoNome);

                    Gruppo gruppo_appena_creato = manager.getGruppo(creazione_gruppoNome);
                    ArrayList<String> username_invitati = parseFromString(inviti2parse);
                    Util.sendinviti(username_invitati, gruppo_appena_creato.getIdgruppo(),manager);
                } catch (SQLException ex) {
                    Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Exception e) {
            }
        }

        chain.doFilter(request, response);

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

    /**
     * Init method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("GroupChangeName:Initializing filter");
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
            return ("GroupChangeName()");
        }
        StringBuffer sb = new StringBuffer("GroupChangeName(");
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
