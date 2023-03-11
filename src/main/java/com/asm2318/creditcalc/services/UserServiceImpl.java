package com.asm2318.creditcalc.services;

import com.asm2318.creditcalc.entities.RequestHistory;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.enums.Authority;
import com.asm2318.creditcalc.enums.CalculationResult;
import com.asm2318.creditcalc.repositories.RequestHistoryRepository;
import com.asm2318.creditcalc.repositories.RoleRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.asm2318.creditcalc.repositories.UserRepository;
import com.asm2318.creditcalc.utils.DateHelper;
import com.asm2318.creditcalc.utils.FieldName;
import com.asm2318.creditcalc.utils.FormName;
import com.asm2318.creditcalc.utils.JsonConverter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository; 
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RequestHistoryRepository requestHistoryRepository;
     
    @Transactional
    @Override
    public User registerNewUser(
            @Nonnull final String username,
            @Nonnull final String email,
            @Nonnull final String password
    ) {
        final StringBuilder errorMessage = new StringBuilder();
        if (emailExists(email))  
            errorMessage.append(String.format("Email [%s] уже существует.\n", email));
        if (usernameExists(username))  
            errorMessage.append(String.format("Логин [%s] уже существует.", username));
        if (errorMessage.length() != 0)
            throw new IllegalArgumentException(errorMessage.toString());

        return userRepository.save(new User(
                username,
                email,
                generatePassword(password),
                true,
                roleRepository.findByRoleCode(Authority.USER.name())
        )); 
    }
    
    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }
    
    private boolean usernameExists(final String username) {
        return userRepository.findByUsername(username) != null;
    }
    
    private String generatePassword(final String password){
        return new BCryptPasswordEncoder().encode(password);
    }
    
    @Override
    @Nullable
    public Map<String, Object> calculatePayments (
            final long userId, 
            final String ipAddress,
            final String uri,
            final String firstPayDate,
            final double loan,
            final double rate,
            final boolean annuity,
            final int term
    ) throws UnsupportedEncodingException{
        final Map<String, Object> response = new HashMap<>();
        String calculationResult;
        try {
            calculationResult = JsonConverter.convertPaymentSchedule(
                    new CalculatorService().calculate(firstPayDate, loan, rate, annuity, term),
                    DateHelper.reformatDate(firstPayDate)
            );
            response.put(FieldName.RESULT, calculationResult);
            response.put(FieldName.MVC_FIELD, FormName.CALCULATION_RESULT_PAGE);
        } catch (final Exception ex) {
            calculationResult = null;
            response.put(FieldName.ERROR_MESSAGE, String.format("Ошибка: %s", ex.getMessage()));
            response.put(FieldName.MVC_FIELD, FormName.CALCULATION_DATA_PAGE);
        }
        saveRequestHistory(
                userId,
                ipAddress,
                uri,
                String.format(
                        "[%s: %s], [%s: %.2f], [%s: %.2f], [%s: %b], [%s: %d]",
                        FieldName.CALCULATOR_DATE, firstPayDate,
                        FieldName.CALCULATOR_SUM, loan,
                        FieldName.CALCULATOR_RATE, rate,
                        FieldName.CALCULATOR_ANNUITY, annuity,
                        FieldName.CALCULATOR_TERM, term
                ),
                calculationResult != null,
                calculationResult != null ? calculationResult.getBytes("UTF-8").length : 0
        );
        
        response.put(FieldName.CALCULATOR_SUM, loan);
        response.put(FieldName.CALCULATOR_RATE, rate);
        response.put(FieldName.CALCULATOR_ANNUITY, Boolean.toString(annuity));
        response.put(FieldName.CALCULATOR_TERM, term);
        response.put(FieldName.CALCULATOR_DATE, firstPayDate);
        return response;
    }
    
    private void saveRequestHistory (
            final long userId, 
            final String ipAddress, 
            final String uri, 
            final String params, 
            final boolean success, 
            final int resultSize
    ){
        requestHistoryRepository.save(new RequestHistory(
                LocalDateTime.now(),
                userId,
                ipAddress,
                uri,
                params,
                success ? CalculationResult.SUCCESS.getId() : CalculationResult.FAILURE.getId(),
                resultSize
        ));
    }
    
}
