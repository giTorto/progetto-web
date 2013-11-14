/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.sql.Date;

/**
 *
 * @author Giulian
 */
class Post {
    private Date data_ora;
    private String testo;
    private Utente writer;

    public Date getData_ora() {
        return data_ora;
    }

    public String getTesto() {
        return testo;
    }

    public Utente getWriter() {
        return writer;
    }

    public void setData_ora(Date data_ora) {
        this.data_ora = data_ora;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public void setWriter(Utente writer) {
        this.writer = writer;
    }
    
    
}
