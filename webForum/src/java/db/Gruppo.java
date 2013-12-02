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
public class Gruppo {

    private String nome;
    private Date dataCreazione;
    private String ownerName;
    private int idgruppo;

    public int getIdgruppo() {
        return idgruppo;
    }

    public String getNome() {
        return nome;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public void setIdgruppo(int idgruppo) {
        this.idgruppo = idgruppo;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Gruppo() {
    }

    public void tohtmlrow(PrintWriter out, Utente u) {
        out.println("                    <tr>");
        out.println("                        <td>" + this.getNome() + "</td>");
        out.println("                        <td>" + this.getDataCreazione() + "</td>");
        out.println("			<td><a href=\"" + "/webForum/logg/displaygroup?"
                + "groupID=" + getIdgruppo() +"\">vai al gruppo</a> </td>");
        out.println("			<td>");
        if (this.getOwnerName().equals(u.getUserName())) {
            
            out.println("			<form action=\"modificaGruppo\" method=\"post\" name=\"altergroup\">");
            out.println("				<button class=\"btn btn-default\" type=\"submit\">Modifica</button>");
            out.println("				<input name=\"groupID\" type=\"hidden\" value=\"" + getIdgruppo() + "\" />");
            out.println("			</form>");
            out.println("			</td>");
            out.println("			<td>");
            out.println("			<form action=\"pdfservlet\" method=\"POST\" name=\"gotopdfwriter\">");
            out.println("				<button class=\"btn btn-default\" type=\"submit\">PDF</button>");
            out.println("				<input name=\"groupID\" type=\"hidden\" value=\"" + getIdgruppo() + "\" />");
            out.println("			</form>");
            out.println("			</td>");
        }
        out.println("");
        out.println("                    </tr>");

    }

}
