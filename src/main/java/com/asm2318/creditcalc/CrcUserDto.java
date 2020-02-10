/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import com.asm2318.creditcalc.CrcValidEmail;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@CrcPasswordMatches
public class CrcUserDto {
    @NotNull
    @NotEmpty
    private String login;
    
    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;
    
    @CrcValidEmail
    @NotNull
    @NotEmpty
    private String email;
    
    @NotNull
    @NotEmpty
    private List<CrcRole> roles;
    
    public String getLogin(){
        return login;
    }
    public void setLogin(String login){
        this.login = login;
    }
    
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getMatchingPassword(){
        return matchingPassword;
    }
    public void setMatchingPassword(String matchingPassword){
        this.matchingPassword = matchingPassword;
    }
    
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    
    public List<CrcRole> getRoles(){
        return roles;
    }
    public void setRoles(List<CrcRole> roles){
        this.roles = roles;
    }
    
}
