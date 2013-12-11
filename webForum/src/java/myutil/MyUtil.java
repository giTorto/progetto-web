/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myutil;

import db.DBManager;
import db.Gruppo;
import db.Utente;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import servlet.gruppiSrvlt;

/**
 *
 * @author Lorenzo
 */
public class MyUtil {

    public static ArrayList<String> sendinviti(ArrayList<String> usernames, int idgruppo, DBManager manager) {
        ArrayList<String> utentiNonEsistenti = new ArrayList<String>();
        boolean noerr = true;
        for (String username : usernames) {
            try {
                Utente u = new Utente();
                u = manager.getMoreByUserName(username);

                if (u != null && !manager.controllaInvitogià_esistente(idgruppo, u.getId())) {
                    Gruppo invitante = manager.getGruppo(idgruppo);
                    String nomeownerinvitante = invitante.getOwnerName();
                    Utente owner = manager.getMoreByUserName(nomeownerinvitante);
                    if (u.getId() != owner.getId()) {
                        try {
                            manager.insertInvito(idgruppo, u.getId());
                        } catch (SQLException ex) {
                            Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    //System.err.println("Util.sendinviti: Impossibile invitare " + username);
                    utentiNonEsistenti.add(username);
                    noerr = false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Util.sendinviti: Impossibile invitare " + username);
            }
        }
        if (noerr){
            return null;
        }else{
             return utentiNonEsistenti;
        }
       
    }

    public static ArrayList<String> parseFromString(String phrase_inviti) {
        ArrayList<String> retval = new ArrayList<String>();
        retval.clear();
        String delims = "[,]";
        String[] tokens = phrase_inviti.split(delims);
        retval.addAll(Arrays.asList(tokens));
        return retval;

    }

    
         /**
     * Su alcuni browser (IE) il file viene caricato con il path assoluto,
     * quindi bisogna recuperare il solo nome del file
     *
     * @param fileName
     * @return vero nome del file
     */
    public static String formatName(String fileName) {
        int index = fileName.lastIndexOf("\\");
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return fileName;
    }


     /**
     * Dato un file, trova la sua estensione
     *
     * @param fileName nome del file da controllare
     * @return estensione del file in input
     */
    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return (index >= 0) ? fileName.substring(index) : "";

        }


}
