/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ROLES")
public class CrcRole {

    @Id
    private String role;
    
    public CrcRole(){}
    
    public CrcRole(String role){
        this.role = role;
    }
    

    @OneToOne
    @JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "ROLE"), inverseJoinColumns = @JoinColumn(name = "USERNAME"))
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
