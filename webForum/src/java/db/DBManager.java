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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

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
                = con.prepareStatement("SELECT g.idgruppo,g.nome,g.idowner,g.datacreazione,grp.idutente,u.username,grp.invito_acc \n"
                        + "FROM (gruppo g INNER JOIN gruppi_partecipanti grp ON grp.idgruppo = g.idgruppo) INNER JOIN utente u on grp.idutente=u.idutente\n"
                        + "WHERE grp.idutente=? AND grp.invito_acc=0");

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
     * Permette di ottenere facilmente la lista di tutti i post di un gruppo ora
     * perfezionata, in ogni post c'è un oggetto Utente che è il writer
     *
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
                    //Utente tu = getMoreUtente(rs.getInt("idwriter"));
                    p.setTesto(rs.getString("testo"));
                    p.setData_ora(rs.getDate("data_ora"));
                    p.setWriter(getMoreUtente(rs.getInt("idwriter")));
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
     * Va chiamata per impostare nel db, la conferma di appartenenza ad un
     * gruppo da parte dell'utente Intuitivamente, la funzione dovrà essere
     * chiamata per ogni checkbox selezionata
     *
     * @param u l'utente che sta accettando l'invito
     * @param idgruppo qui devi passare l'idgruppo di cui hai accettato l'invito
     * @throws SQLException
     *
     * @nota puoi anche decidere di fare che nella inviti c'è una form per ogni
     * riga e poi una volta premuto devi aggiornare la pagina con gli inviti
     * ancora non accettati
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
     *
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
     * Funzione complementare alla creazione gruppo questa si occupa di mandare
     * gli inviti per il nuovo gruppo, ovviamente bisogna darli la List<integer>
     * degli id, in caso di problemi la funzione può essere facilmente sistemata
     * su un id alla volta
     *
     * @param idinvitati la List<Integer> di tutti gli id di quelli che vuoi
     * invitare
     * @param idgruppo l'id del gruppo a cui stai per invitare i tuoi amichetti
     * @throws SQLException Se ci sono problemi potrebbe essere la chiusura e la
     * riapertura dello stm
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
     *
     * @param u devi dare in input l'utente della sessione attuale
     * @param idgruppo devi dare in input l'id del gruppo che stai visualizzando
     * @param testo qui va il testo che deve essere inserito nel post
     * @throws SQLException Se ti stai chiedendo del file, beh quello non va nel
     * DB, e la data la trova automaticamente
     */
    public void aggiungiPost(Utente u, int idgruppo, String testo) throws SQLException {
        int idutente = u.getId();

        Date data = new Date(Calendar.getInstance().getTimeInMillis());

        PreparedStatement stm
                = con.prepareStatement("INSERT INTO POST (data_ora,testo,idwriter,idgruppo) values(?,?,?,?) ");

        try {
            stm.setDate(1, data);
            stm.setString(2, testo);
            stm.setInt(3, idutente);
            stm.setInt(4, idgruppo);
            int executeUpdate = stm.executeUpdate();
        } catch (SQLException ex) {

        } finally {
            stm.close();
        }
    }

    /**
     * Dato un idgruppo restituisce un oggetto contenente tutte le sue info
     *
     * @param Idgruppo
     * @return oggetto
     * @throws SQLException
     */
    public Gruppo getGruppo(int Idgruppo) throws SQLException {
        Gruppo group = new Gruppo();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM gruppo g where g.idgruppo=?");
        try {
            stm.setInt(1, Idgruppo);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {

                    group.setNome(rs.getString("nome"));
                    group.setDataCreazione(rs.getDate("datacreazione"));
                    group.setIdgruppo(rs.getInt("idgruppo"));
                    group.setOwnerName(getMoreUtente(rs.getInt("idowner")).getUserName());

                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return group;
    }

    /**
     * GeneraPDF: prima delle funzioni, prende dal db le informazioni necessarie
     * per restituire la lista utenti in ordine alfabetico
     *
     * @param idgruppo è il campo necessario per riconoscere in modo univoco il
     * gruppo
     * @return restituisce la lista degli id degli utenti che partecipano al
     * gruppo
     * @NotaBENE non restituisce l'owner del gruppo, gli id restituiti sono in
     * ordine alfabetico
     * @throws SQLException
     */
    public List<Integer> getUtenti(int idgruppo) throws SQLException {

        List<Integer> allUsers = new ArrayList<Integer>();
        PreparedStatement stm
                = con.prepareStatement("SELECT * FROM gruppi_partecipanti "
                        + "                        WHERE idgruppo = ? and invito_acc=1");

        try {
            stm.setInt(1, idgruppo);
            ResultSet rs = stm.executeQuery();

            try {

                while (rs.next()) {
                    Integer ut = new Integer(0);
                    ut = rs.getInt("idutente");

                    allUsers.add(ut);
                }
            } finally {

                rs.close();
            }
        } finally {

            stm.close();
        }

        return allUsers;
    }

    /**
     * Funzione accessoria di generaPDF Dato un idgruppo e un idutente dice
     * quanti post ha effettuato
     *
     * @param idgruppo l'identificatore del grupp
     * @param idutente l'identificatore dell'utente
     * @return restituisce il numero degli utenti
     * @throws SQLException
     * @NOTABENE se usata sulla lista degli id restituita da getUtenti permette
     * di sapere quanti post ha fatto ogni utente
     */
    public int getPostPerUtente(int idgruppo, int idutente) throws SQLException {
        int postPerUtente = 0;

        PreparedStatement stm
                = con.prepareStatement("SELECT COUNT(p.idwriter) "
                        + "FROM post p  "
                        + "WHERE g.idgruppo = ? "
                        + "AND p.idwriter = ?");

        try {
            stm.setInt(1, idgruppo);
            stm.setInt(2, idutente);
            ResultSet rs = stm.executeQuery();

            postPerUtente = rs.getInt(1);
            rs.close();

        } finally {

            stm.close();
        }

        return postPerUtente;

    }

    /**
     * Generapdf: seconda funz, Restituisce il numero di post totali effettuati
     * nel gruppo
     *
     * @param idgruppo l'identificatore del gruppo
     * @return restituisce un numero intero con tutti i post effettuati
     * @throws SQLException
     */
    public int getNumPostPerGruppo(int idgruppo) throws SQLException {

        PreparedStatement stm
                = con.prepareStatement("SELECT COUNT (p.idgruppo) AS count "
                        + "FROM post p  "
                        + "WHERE p.idgruppo = ?");

        try {
            stm.setInt(1, idgruppo);
            ResultSet resultSet = stm.executeQuery();

            try {
                if (resultSet.next()) {
                    return resultSet.getInt("count");
                } else {
                    return 0;
                }

            } finally {
                resultSet.close();
            }
        } finally {
            stm.close();
        }

    }

    /**
     * Generapdf: Permette di trovare qual'è la data dell'ultimo post di un
     * certo gruppo
     *
     * @param idgruppo di quale gruppo si vuole sapere la data dell'ultimo post
     * @return restituisce null in caso non ci sia mai stato nessun post
     * @throws SQLException
     */
    public Date getDataUltimoPost(int idgruppo) throws SQLException {
        Date data = null;

        PreparedStatement stm = con.prepareStatement("SELECT max(data_ora) as maxdata from post where idgruppo = ? ");

        try {
            stm.setInt(1, idgruppo);
            ResultSet resultSet = stm.executeQuery();

            try {
                if (resultSet.next()) {
                    return resultSet.getDate("maxdata");
                } else {
                    return null;
                }

            } finally {
                resultSet.close();
            }
        } finally {
            stm.close();
        }

    }

    public void updateGroupName(int idgroup, String nuovo_nome) throws SQLException {

        PreparedStatement stm = con.prepareStatement("UPDATE GRUPPO   SET NOME = ?  WHERE IDGRUPPO = ?");
        try {
            stm.setString(1, nuovo_nome);
            stm.setInt(2, idgroup);

            int executeUpdate = stm.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Errore nell'aggiornare il gruppo con id:" + idgroup);
        } finally {
            stm.close();
        }
    }

    public void updatePartecipanti(int idutente, int id_gruppo_accettato) throws SQLException {
        PreparedStatement stm = con.prepareStatement("UPDATE GRUPPI_PARTECIPANTI   SET INVITO_ACC = 1  WHERE IDUTENTE=? AND idgruppo=?");
        try {
            stm.setInt(1, idutente);
            stm.setInt(2, id_gruppo_accettato);
            int executeUpdate = stm.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Errore nell'aggiornare i partecipanti");
        } finally {
            stm.close();
        }
    }

    /**
     * Aggiunge un nuovo file al DB
     *
     * @param user l'utente che ha aggiunto il post
     * @param idgruppo ID del gruppo in cui è stato posto
     * @param realname nome del file secondo l'utente
     * @param dbname nome univoco del file generato automaticamente
     * @param testo il testo del post
     */
    public void addPostFile(Utente user, int idgruppo, String realname, String dbname, String testo) throws SQLException {
        int idutente = user.getId();

        Date data = new Date(Calendar.getInstance().getTimeInMillis());

        PreparedStatement stm
                = con.prepareStatement("INSERT INTO POST (data_ora,testo,idwriter,idgruppo,realname,dbname) values(?,?,?,?,?,?) ");

        try {
            stm.setDate(1, data);
            stm.setString(2, testo);
            stm.setInt(3, idutente);
            stm.setInt(4, idgruppo);
            stm.setString(5, realname);
            stm.setString(6, dbname);
            int executeUpdate = stm.executeUpdate();
        } catch (SQLException ex) {

        } finally {
            stm.close();
        }

    }

    /**
     * Ricerca l'ID del file basandosi sul nome. Se il file viene trovato, viene
     * impostato un campo per indicarlo come recente
     *
     * @param fileName Nome del file
     * @param userName Nome dell'utente da linkare
     * @return Url del file associato al nome Utente o stringa vuota
     */
    public int getLinkByName(String fileName, String user) {
        int retVal = 0;
        try {
            int id = (getMoreByUserName(user).getId());
            ResultSet rs;

            PreparedStatement stm
                    = con.prepareStatement("SELECT * FROM POST WHERE idwriter = ? AND realname=?");
            stm.setInt(1, id);
            stm.setString(2,fileName);
            
            rs = stm.executeQuery();
            retVal = rs.getInt("idpost");
            
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    /**
     * Ricerca l'ID del file basandosi sul nome. Il file ricercato è quello
     * aggiunto per ultimo
     *
     * @param fileName Nome del FIlE da cercare
     * @return ID del file o stringa vuota
     */
    public int getLRULink(String fileName) {
       
       int retVal = 0;
        try {
            
            ResultSet rs;

            PreparedStatement stm
                    = con.prepareStatement("SELECT idpost FROM POST WHERE realname=? ORDER BY data_ora DESC FETCH FIRST 1 ROWS ONLY");
            stm.setString(1,fileName);
            
            rs = stm.executeQuery();
            retVal = rs.getInt("idpost");
            
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    public Utente getMoreByUserName(String nome) throws SQLException {

        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente WHERE username = ?");
        try {
            stm.setString(1, nome);
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
}
