/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.PrintWriter;
import java.sql.Date;

/**
 *
 * @author Giulian
 */
public class Post {

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

    public void tohtmlrow(PrintWriter out, Utente u) {
        Utente us = this.getWriter();
        out.println("		<li><label id='username'>"+us.getUserName()+"</label>:  <label id=\"Label1\">"+this.getTesto()+"</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label id=\"Label1\">"+this.getData_ora().toString()+
                "<label id='linkPost'> </label>");
        out.println("		</label>&nbsp;&nbsp;<label id=\"Label1\">"+/*this.getWriter().getUserName()*/""+"</label><br>");
        out.println("		<br></li>");
    }

}
