package com.asm2318.creditcalc.services;

import com.asm2318.creditcalc.entities.User;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.annotation.Nonnull;

/** Обработка запросов пользователя */
public interface UserService {
    
    /**
     * Регистрирует пользователя
     * @param username  Логин
     * @param email     Адрес электронной почты
     * @param password  Пароль
     * @return Зарегистрированный пользователь
     */
    User registerNewUser(@Nonnull String username, @Nonnull String email, @Nonnull String password);
    
    /**
     * Рассчитывает график погашения кредита и сохраняет его в историю
     * @param userId        Идентификатор пользователя
     * @param ipAddress     ip-адрес запроса
     * @param uri           uri для сохранения в историю
     * @param firstPayDate  Дата первого платежа
     * @param loan          Сумма кредита
     * @param rate          Процентная ставка
     * @param annuity       Аннуитетный или дифференцированный
     * @param term          Срок кредита
     * @return Набор параметров, включая график погашения в формате json
     * @throws UnsupportedEncodingException 
     */
    Map<String, Object> calculatePayments (
            long userId, 
            @Nonnull String ipAddress,
            @Nonnull String uri,
            @Nonnull String firstPayDate,
            double loan,
            double rate,
            boolean annuity,
            int term
    ) throws UnsupportedEncodingException;
    
}
