/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CrcPasswordValidator implements ConstraintValidator<CrcPasswordMatches, Object>{
    
    @Override
    public void initialize(CrcPasswordMatches constraintAnnotation){       
    }
    
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){   
        CrcUserDto user = (CrcUserDto)obj;
        return user.getPassword().equals(user.getMatchingPassword());    
    } 
}
