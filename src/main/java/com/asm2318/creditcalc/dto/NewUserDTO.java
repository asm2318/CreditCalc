package com.asm2318.creditcalc.dto;

import com.asm2318.creditcalc.validators.ValidPassword;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.asm2318.creditcalc.validators.ValidEmail;

/** Создание нового пользователя, эксперимент с валидаторами*/
@ValidPassword
public class NewUserDTO {
    
    /** Логин */
    @NotNull
    @NotEmpty
    private String username;
    /** Пароль */
    @NotNull
    @NotEmpty
    private String password;
    /** Подтвержденный пароль */
    private String matchingPassword;
    /** Адрес электронной почты */
    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;
    
    public String getUsername(){
        return username;
    }
    public void setUsername(final String username){
        this.username = username;
    }
    
    public String getPassword(){
        return password;
    }
    public void setPassword(final String password){
        this.password = password;
    }
    
    public String getMatchingPassword(){
        return matchingPassword;
    }
    public void setMatchingPassword(final String matchingPassword){
        this.matchingPassword = matchingPassword;
    }
    
    public String getEmail(){
        return email;
    }
    public void setEmail(final String email){
        this.email = email;
    }
    
}
