package com.asm2318.creditcalc.services;

import com.asm2318.creditcalc.entities.RequestHistory;
import com.asm2318.creditcalc.entities.Role;
import com.asm2318.creditcalc.entities.User;
import java.util.List;
import javax.annotation.Nullable;

/** Обработка запросов администратора */
public interface AdministratorService {
    
    /**
     * Формирует истории расчетов кредитного калькулятора
     * @param dateStart Начало периода
     * @param dateEnd   Конец периода
     * @return История расчетов
     */
    List<RequestHistory> getRequestsHistory(@Nullable String dateStart, @Nullable String dateEnd);
    
    /**
     * Формирует список всех пользователей
     * @return Список всех пользователей
     */
    List<User> getUsersList();
    
    /**
     * Обновляет роль пользователя
     * @param userId Идентификатор пользователя
     * @param roleId Идентификатор роли
     */
    void updateUserRole(Long userId, Long roleId);
    
    /**
     * Формирует список всех доступных ролей
     * @return Список всех ролей
     */
    List<Role> getRolesList();
    
}
