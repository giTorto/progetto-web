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
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

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
        out.println("		<li><label id='username'><strong>" + us.getUserName() + "</strong></label>:  <label id=\"Label1\"><em>" + this.getTesto() + "</em></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label id=\"Label1\"><sub>" + this.getData_ora().toString()
                + "</sub><label id='linkPost'> </label>");
        out.println("		</label>&nbsp;&nbsp;<label id=\"Label1\">" +/*
                 * this.getWriter().getUserName()
                 */ "" + "</label><br>");
        if (this.getLink() != null) {
            out.println("<label> " + this.getLink() + "</label>");
        }
        out.println("		<br></li>");

    }

}
