/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

public class CrcHashData {
    private String login;
    int hashpswrd;
    
    public String getLogin(){
        return login;
    }
    public void setLogin(String login){
        this.login = login;
    }
    
    public int getHashpswrd(){
        return hashpswrd;
    }
    public void setHashpswrd(int hashpswrd){
        this.hashpswrd = hashpswrd;
    }
}
