package com.asm2318.creditcalc.entities;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Пользователь */
@Entity
@Table(name="CC_USERS")
public class User implements UserDetails{
    
    /**Идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;
    /** Логин */
    private String username;
    
    private String email;
    
    private String password;
    
    private boolean enabled;
    
    @OneToOne
    @JoinTable(
            name = "CC_USERS_ROLES_XREF", 
            joinColumns = @JoinColumn(name = "USER_ID"), 
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Role role;
    
    public User(){}
    
    public User(
            final String username, 
            final String email, 
            final String password, 
            final boolean enabled, 
            final Role role
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    
    public void setUserId(final Long userId) { this.userId = userId; }
    
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String login) {
        this.username = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
