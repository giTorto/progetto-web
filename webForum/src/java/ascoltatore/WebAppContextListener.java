/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ascoltatore;
import db.DBManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 *
 * @author Giulian
 */
public class WebAppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
          String dburl = sce.getServletContext().getInitParameter("dburl");
 
       try {
           DBManager manager = new DBManager(dburl);
           sce.getServletContext().setAttribute("dbmanager", manager);//pubblico l'attributo per il context

        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).severe(ex.toString());
            throw new RuntimeException(ex);
 
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
   
        DBManager.shutdown();
    }
    
}
