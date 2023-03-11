package com.asm2318.creditcalc.utils;

import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.enums.Authority;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AuthorityHelper {
    
    public static boolean userHasAuthority(@Nonnull final Object userObject, @Nonnull final Authority authority) {
        if (!(userObject instanceof User))
            throw new RuntimeException("Неверный тип пользователя.");
        return Objects.requireNonNull(((User)userObject).getRole()).getAuthority().equals(authority.name());
    }
    
}
