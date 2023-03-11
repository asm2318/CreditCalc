package com.asm2318.creditcalc.repositories;

import com.asm2318.creditcalc.entities.User;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, String> {
    
    /**
     * Ищет пользователя по адресу электронной почты
     * @param email Адрес электронной почты
     * @return Пользователь
     */
    User findByEmail(String email);
    
    /**
     * Ищет пользователя по логину
     * @param username Логин
     * @return Пользователь
     */
    User findByUsername(String username);
    
    /**
     * Ищет всех пользователей, сортирует по логинам
     * @return Список всех пользователей
     */
    List<User> findAllByOrderByUsername();
    
    /**
     * Ищет пользователя с блокировкой по идентификатору
     * @param userId Идентификатор пользователя
     * @return Пользователь
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByUserId(long userId);
    
}
