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
public class Gruppo {
    private String nome;
    private Date dataCreazione;
    private String ownerName;

    public String getNome() {
        return nome;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

   /* public String getOwnerName() {
        return ownerName;
    }
    */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    /*
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    } */

    public Gruppo() {
    }
    
   
    
    
}
