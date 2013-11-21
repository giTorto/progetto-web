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
        out.println("                        <td>1</td>");
        out.println("                        <td>" + this.getNome() + "</td>");
        out.println("                        <td>" + this.getDataCreazione() + "</td>");
        out.println("                        <td>somelink</td>");
        out.println("                        <td><button type=\"submit\" class=\"btn btn-default\">Modifica</button></td>");
        out.println("                        <td> <form name=\"gotopdfwriter\" action=\"pdfservlet\" method=\"POST\">");
        out.println("                                <button type=\"submit\" class=\"btn btn-default\">PDF</button>");
        out.println("                            </form>  </td>");
        out.println("");
        out.println("                    </tr>");
    }

}
