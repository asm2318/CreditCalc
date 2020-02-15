/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.util.Arrays;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CrcUserServiceImpl implements CrcUserService {
    @Autowired
    private CrcUserRepository repository; 
     
    @Transactional
    @Override
    public CrcUser registerNewUserAccount(CrcUserDto userDto) throws Exception {
         
        if (emailExist(userDto.getEmail())) {  
            throw new Exception("Указанный адрес электронной почты уже зарегистрирован.");
        }
        if (loginExist(userDto.getLogin())) {  
            throw new Exception("Указанный логин уже зарегистрирован.");
        }

        CrcUser user = new CrcUser();    
        user.setUsername(userDto.getLogin());
        user.setPassword(passwordGenerator(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setEnabled("true");
        user.setRole(new CrcRole("USER"));
        return repository.save(user); 
    }
    
    private boolean emailExist(String email) {
        CrcUser user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }
    
    private boolean loginExist(String login) {
        CrcUser user = repository.findByUsername(login);
        if (user != null) {
            return true;
        }
        return false;
    }
    
    public String passwordGenerator(String password){
        String result = new BCryptPasswordEncoder().encode(password);
        return result;
    }
}
