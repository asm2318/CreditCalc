package com.asm2318.creditcalc;

import com.asm2318.creditcalc.dto.NewUserDTO;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.repositories.RequestHistoryRepository;
import com.asm2318.creditcalc.repositories.UserRepository;
import com.asm2318.creditcalc.services.UserService;
import com.asm2318.creditcalc.validators.LoanParameter;
import java.io.UnsupportedEncodingException;
import org.flywaydb.core.Flyway;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserServiceTest {
    
    @Autowired
    private Flyway flyway;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RequestHistoryRepository requestHistoryRepository;
    
    @BeforeEach
    private void createDB() {
        flyway.clean();
        flyway.migrate();
    }
    
    private NewUserDTO prepareUser () {
        final NewUserDTO newUser = new NewUserDTO();
        newUser.setUsername("user1");
        newUser.setEmail("user@mail.com");
        newUser.setPassword("psw");
        return newUser;
    }
    
    
    @Test
    @Transactional
    public void registerNewUser() {
        final NewUserDTO newUser = prepareUser();
        userService.registerNewUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());
        final User savedUser = userRepository.findByUserId(1).get();
        assertEquals(savedUser.getUsername(), newUser.getUsername());
        assertEquals(savedUser.getEmail(), newUser.getEmail());
        assertTrue(BCrypt.checkpw(newUser.getPassword(), savedUser.getPassword()));
    }
    
    @Test
    public void checkUserExists() {
        final NewUserDTO newUser = prepareUser();
        userService.registerNewUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());
        
        newUser.setUsername("user2");
        assertThrows(
                IllegalArgumentException.class, 
                () -> {userService.registerNewUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());}
        );
        
        newUser.setUsername("user1");
        newUser.setEmail("user1@mail.com");
        assertThrows(
                IllegalArgumentException.class, 
                () -> {userService.registerNewUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());}
        );
    }
    
    @Test
    public void checkCalculationHistory() throws UnsupportedEncodingException {
        final NewUserDTO newUser = prepareUser();
        final User user = userService.registerNewUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());
        userService.calculatePayments(user.getUserId(), "127.0.0.1", "/calculate", "2020-01-01", 100000, 12, true, 12);
        assertTrue(requestHistoryRepository.findAllByOrderByCreateTs().stream().findAny().get().getResultSize() > 0);
        userService.calculatePayments(
                user.getUserId(), 
                "127.0.0.1", 
                "/calculate", 
                "2020-01-01", 
                LoanParameter.SUM_MAX, 
                LoanParameter.RATE_MAX, 
                true, 
                LoanParameter.TERM_MAX
        );
        assertTrue(
                requestHistoryRepository.findAllByOrderByCreateTs().stream().filter(h -> h.getResultSize() == 0)
                        .findAny().isPresent()
        );
    }
}
