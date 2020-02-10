/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="USERS")
public class CrcUser {

    private String username;
    private String email;
    private String password;
    private String enabled;
    private List<CrcRole> roles;
    

    public CrcUser(){}

    public CrcUser(String username, String email, String password, String enabled) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public CrcUser(String username, String email, String password, List<CrcRole> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Id
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @ManyToMany
    @JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "USERNAME"), inverseJoinColumns = @JoinColumn(name = "ROLE"))
    public List<CrcRole> getRoles() {
        return roles;
    }

    public void setRoles(List<CrcRole> roles) {
        this.roles = roles;
    }
}
