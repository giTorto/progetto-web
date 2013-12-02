/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import db.DBManager;
import db.Utente;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import servlet.gruppiSrvlt;

/**
 *
 * @author Lorenzo
 */
public class Util {
    
    public  static void sendinviti(ArrayList<String> usernames, int idgruppo, DBManager manager) {
        for (String username : usernames) {
            try {
                Utente u = new Utente();
                u = manager.getMoreByUserName(username);

                if (!manager.controllaInvitogià_esistente(idgruppo, u.getId())) {
                    try {
                        manager.insertInvito(idgruppo, u.getId());
                    } catch (SQLException ex) {
                        Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(gruppiSrvlt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
