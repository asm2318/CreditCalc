package com.asm2318.creditcalc.validators;

import com.asm2318.creditcalc.dto.NewUserDTO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, Object>{
    
    @Override
    public void initialize(final ValidPassword constraintAnnotation){       
    }
    
    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context){   
        final NewUserDTO user = (NewUserDTO)obj;
        return user.getPassword().equals(user.getMatchingPassword());    
    } 
}
