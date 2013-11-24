/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

    public List<Gruppo> getInvitiGruppi(Utente u) throws SQLException {
        List<Gruppo> gruppi = new ArrayList<Gruppo>();
        int id = u.getId();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM (gruppo g"
                        + " INNER JOIN gruppi_partecipanti gr ON gr.idgruppo = g.idgruppo) INNER JOIN utente u"
                        + " ON gr.idutente = u.idutente"
                        + " WHERE u.idutente = ? "
                        + " AND gr.invito_acc = 0");

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

    public Utente getMoreUtente(int id) throws SQLException {

        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente WHERE idutente = ?");
        try {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            try {
                if (rs.next()) {
                    Utente user = new Utente();
                    user.setUserName(rs.getString("username"));

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

    /**
     * Permette di ottenere facilmente la lista di tutti i post di un gruppo
     * @param g dai in input il gruppo di cui vuoi vedere i post
     * @return ricevi la lista dei post in ordine di data inversa
     * @throws SQLException
     */
    public List<Post> getPostsGruppo(Gruppo g) throws SQLException {

        List<Post> posts = new ArrayList<Post>();
        int id = g.getIdgruppo();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM post "
                        + "WHERE idgruppo = ? ORDER BY data_ora DESC");

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
    
    /**
     *  Va chiamata per impostare nel db, la conferma di appartenenza ad un gruppo da parte dell'utente
     * Intuitivamente, la funzione dovrà essere chiamata per ogni checkbox selezionata
     * @param u l'utente che sta accettando l'invito
     * @param idgruppo qui devi passare l'idgruppo di cui hai accettato l'invito
     * @throws SQLException 
     * 
     * @nota puoi anche decidere di fare che nella inviti c'è una form per ogni riga e poi una volta
     * premuto devi aggiornare la pagina con gli inviti ancora non accettati
     */
    public void setAccettaInvito(Utente u, int idgruppo) throws SQLException {
        int idutente = u.getId();

        PreparedStatement stm
                = con.prepareStatement("UPDATE gruppi_partecipanti SET invito_acc=1 WHERE idutente =? AND idgruppo = ?");

        try {
            stm.setInt(1, idutente);
            stm.setInt(2, idgruppo);
            ResultSet rs = stm.executeQuery();
        } finally {
            stm.close();
        }

    }

    /**
     * Creazione di un gruppo dando in input utente e nome del gruppo
     * @param u qui serve dare in input l'utente che sta chiamando la funzione
     * cioè chi sta creando il gruppo
     * @param nome qui bisogna mettere il nome che si vuole dare al gruppo La
     * data la prende appena chiamata. Dopo questa funzione è necessario
     * @see inviteAllYouDesire passandoli un List<Integer> idInvitati
     */
    public void creaGruppo(Utente u, String nome) throws SQLException {
        int idutente = u.getId();
        Date data = new Date(Calendar.getInstance().getTimeInMillis());

        PreparedStatement stm
                = con.prepareStatement("INSERT INTO gruppo (nome,datacreazione,idowner) values(?,?,?) ");

        try {
            stm.setString(1, nome);
            stm.setDate(2, data);
            stm.setInt(3, idutente);
            ResultSet rs = stm.executeQuery();
        } finally {
            stm.close();
        }

    }

    /**
     * Funzione complementare alla creazione gruppo questa si occupa di mandare gli inviti per il nuovo
     * gruppo, ovviamente bisogna darli la List<integer> degli id, in caso di problemi la
     * funzione può essere facilmente sistemata su un id alla volta
     * @param idinvitati la List<Integer> di tutti gli id di quelli che vuoi
     * invitare
     * @param idgruppo l'id del gruppo a cui stai per invitare i tuoi amichetti
     * @throws SQLException Se ci sono problemi potrebbe essere la chiusura e la riapertura dello
     * stm
     */
    public void inviteAllYouDesire(List<Integer> idinvitati, int idgruppo) throws SQLException {
        PreparedStatement stm;

        for (Integer idinvitato : idinvitati) {

            stm = con.prepareStatement("Insert into gruppi_partecipanti (idutente, invito_acc,idgruppo) values (?,0,?)");

            try {
                stm.setInt(1, idinvitato);
                stm.setInt(3, idgruppo);
                ResultSet rs = stm.executeQuery();
            } finally {
                stm.close();
            }
        }

    }

    /**
     * Permette l'inserimento di un post nel database
     * @param u devi dare in input l'utente della sessione attuale
     * @param idgruppo devi dare in input l'id del gruppo che stai visualizzando
     * @param testo qui va il testo che deve essere inserito nel post
     * @throws SQLException 
     * Se ti stai chiedendo del file, beh quello non va nel DB, e la data la trova automaticamente
     */
    public void aggiungiPost(Utente u, int idgruppo, String testo) throws SQLException {
        int idutente = u.getId();
       
        
        Date data = new Date(Calendar.getInstance().getTimeInMillis());

        PreparedStatement stm
                = con.prepareStatement("INSERT INTO gruppo (data_ora,testo,idwriter,idgruppo) values(?,?,?,?) ");

        try {
            stm.setDate(1, data);
            stm.setString(2, testo);
            stm.setInt(3, idutente);
            stm.setInt(4, idgruppo);
            ResultSet rs = stm.executeQuery();
        } finally {
            stm.close();
        }
    }
    
    
}
