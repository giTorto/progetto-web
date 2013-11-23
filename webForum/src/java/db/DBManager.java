/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Giulian
 */
public class DBManager implements Serializable {

    private transient Connection con;//transient = non serializzabile

    public DBManager(String dburl) throws SQLException {

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver", true, getClass().getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

        Connection con = DriverManager.getConnection(dburl);

        this.con = con;
    }

    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {

            Logger.getLogger(DBManager.class.getName()).info(ex.getMessage());

        }

    }

    /**
     * Autentica un utente in base a un nome utente e a una password
     *
     * @param username il nome utente
     * @param password la password
     * @return null se l'utente non è autenticato, un oggetto User se l'utente
     * esiste ed è autenticato
     * @throws java.sql.SQLException
     */
    public Utente authenticate(String username, String password) throws SQLException {

        // usare SEMPRE i PreparedStatement, anche per query banali. 
        // *** MAI E POI MAI COSTRUIRE LE QUERY CONCATENANDO STRINGHE !!!! 
        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente WHERE username = ? AND password = ?");
        try {
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();

            try {
                if (rs.next()) {
                    Utente user = new Utente();
                    user.setUserName(username);
                    user.setAvatar(rs.getString("avatar"));
                    user.setId(rs.getInt("idutente"));
                    return user;
                } else {
                    return null;

                }

            } finally {
                // ricordarsi SEMPRE di chiudere i ResultSet in un blocco finally 
                rs.close();
            }

        } finally { // ricordarsi SEMPRE di chiudere i PreparedStatement in un blocco finally
            stm.close();
        }

    }

    public List<Gruppo> getGruppiOwner(Utente u) throws SQLException {

        List<Gruppo> gruppi = new ArrayList<Gruppo>();
        int id = u.getId();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM gruppo g, utente u where u.idutente = g.idowner and u.idutente =? ");

        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {

                while (rs.next()) {
                    Gruppo p = new Gruppo();
                    p.setNome(rs.getString("nome"));
                    p.setDataCreazione(rs.getDate("datacreazione"));
                    p.setIdgruppo(rs.getInt("idgruppo"));
                    p.setOwnerName(u.getUserName());
                    gruppi.add(p);
                }
            } finally {

                rs.close();
            }
        } finally {

            stm.close();
        }

        return gruppi;

    }

    public List<Gruppo> getGruppiPart(Utente u) throws SQLException {

        List<Gruppo> gruppi = new ArrayList<Gruppo>();
      int id = u.getId();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM (gruppo g INNER JOIN gruppi_partecipanti gr ON gr.idgruppo = g.idgruppo)  INNER JOIN utente u ON gr.idutente = u.idutente WHERE u.idutente = ? "
                        + "AND gr.invito_acc > 0");

        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {

                while (rs.next()) {
                    Gruppo p = new Gruppo();
                    p.setOwnerName(getMoreUtente(rs.getInt("idowner")).getUserName());
                    p.setNome(rs.getString("nome"));
                    p.setDataCreazione(rs.getDate("datacreazione"));
                    p.setIdgruppo(rs.getInt("idgruppo"));
                    gruppi.add(p);

                }
            } finally {

                rs.close();
            }
        } finally {

            stm.close();
        }

        return gruppi;

    }
    
    public List<Gruppo> getInvitiGruppi(Utente u) throws SQLException{
             List<Gruppo> gruppi = new ArrayList<Gruppo>();
      int id = u.getId();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM (gruppo g"
                        + " INNER JOIN gruppi_partecipanti gr ON gr.idgruppo = g.idgruppo) as s INNER JOIN utente u"
                        + " ON s.idutente = u.idutente"
                        + "WHERE u.idutente = ? "
                        + "AND gr.invito_acc = 0");

        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {

                while (rs.next()) {
                    Gruppo p = new Gruppo();
                    p.setOwnerName(getMoreUtente(rs.getInt("idowner")).getUserName());
                    p.setNome(rs.getString("nome"));
                    p.setDataCreazione(rs.getDate("datacreazione"));
                    p.setIdgruppo(rs.getInt("idgruppo"));
                    gruppi.add(p);

                }
            } finally {

                rs.close();
            }
        } finally {

            stm.close();
        }

        return gruppi;
        
    }
    
    public Utente getMoreUtente(int id) throws SQLException{
    
        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente WHERE idutente = ?");
        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {
                if (rs.next()) {
                    Utente user = new Utente();
                    user.setUserName(rs.getString("username"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setId(rs.getInt("idutente"));
                    return user;
                } else {
                    return null;

                }

            } finally {
                // ricordarsi SEMPRE di chiudere i ResultSet in un blocco finally 
                rs.close();
            }

        } finally { // ricordarsi SEMPRE di chiudere i PreparedStatement in un blocco finally
            stm.close();
        } 
    }
    
    public List<Post> getPostsGruppo(Gruppo g) throws SQLException {

        List<Post> posts = new ArrayList<Post>();
        int id = g.getIdgruppo();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM post "
                        + "WHERE idgruppo = ?");

        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {
                
                while (rs.next()) {
                    Post p = new Post();
                 //   Utente tu = getMoreUtente(rs.getInt("idwriter"));
                    p.setTesto(rs.getString("testo"));
                    p.setData_ora(rs.getDate("data_ora"));
                    //p.setWriter(tu);
                    posts.add(p);
                }
            } finally {

                rs.close();
            }
        } finally {

            stm.close();
        }

        return posts;

    }
}
