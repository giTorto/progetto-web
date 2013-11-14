/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.io.Serializable;

/**
 *
 * @author Giulian
 */
public class Utente implements Serializable {
    private String avatar;
    private String password;
    private String userName;
    private int id;

    public int getId() {
        return id;
    }
    
    
    public String getAvatar(){
    return avatar;
    }
    
    public void setAvatar(String fullName){
        this.avatar = fullName;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
        
    }
    
    public String getUserName(){
        return userName;
    }
    
    public void setUserName(String userName){
        this.userName = userName;
        
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
