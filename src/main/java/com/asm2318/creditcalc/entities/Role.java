package com.asm2318.creditcalc.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

/** Роль пользователя */
@Entity
@Table(name = "CC_ROLES")
public class Role implements GrantedAuthority {
    
    /** Идентификатор роли */
    @Id
    private Long roleId;
    /** Код роли */
    private String roleCode;
    /** Наименование роли */
    private String roleName;
    
    public Role(){}
    
    public Role(final Long roleId, final String roleCode, final String roleName){
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }
    
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(final Long roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleCode() {
        return roleCode;
    }
    
    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }
    
    @Override
    public String getAuthority() { return roleCode; }
}
