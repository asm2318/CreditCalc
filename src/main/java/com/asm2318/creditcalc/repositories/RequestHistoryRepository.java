package com.asm2318.creditcalc.repositories;

import com.asm2318.creditcalc.entities.RequestHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, String> {
    
    /** 
     * Ищет все записи + подтягивает логин пользователя
     * @return Список всех записей в истории
     */
    @Query(
            value = "select"
                    + "\n h.REQUEST_HISTORY_ID"
                    + ",\n h.CREATE_TS"
                    + ",\n h.USER_ID"
                    + ",\n u.USERNAME"
                    + ",\n h.IP_ADDRESS"
                    + ",\n h.URI"
                    + ",\n h.PARAMS"
                    + ",\n h.RESULT_ID"
                    + ",\n h.RESULT_SIZE"
                    + "\n from CC_REQUESTS_HISTORY h, CC_USERS u"
                    + "\n where h.USER_ID = u.USER_ID"
                    + "\n order by h.CREATE_TS", 
            nativeQuery = true
    )
    List<RequestHistory> findAllByOrderByCreateTs();
    
    /** 
     * Ищет записи за период по возрастанию + подтягивает логин пользователя
     * @param dateStart начало периода
     * @param dateEnd   конец периода
     * @return Список записей в истории за период
     */
    @Query(
            value = "select"
                    + "\n h.REQUEST_HISTORY_ID"
                    + ",\n h.CREATE_TS"
                    + ",\n h.USER_ID"
                    + ",\n u.USERNAME"
                    + ",\n h.IP_ADDRESS"
                    + ",\n h.URI"
                    + ",\n h.PARAMS"
                    + ",\n h.RESULT_ID"
                    + ",\n h.RESULT_SIZE"
                    + "\n from CC_REQUESTS_HISTORY h, CC_USERS u"
                    + "\n where h.CREATE_TS >= :dateStart and h.CREATE_TS < :dateEnd and h.USER_ID = u.USER_ID"
                    + "\n order by h.CREATE_TS", 
            nativeQuery = true
    )
    List<RequestHistory> findAllWithRange(
            @Param("dateStart") LocalDate dateStart, 
            @Param("dateEnd")LocalDate dateEnd
    );
    
    /** 
     * Ищет записи за период по убыванию + подтягивает логин пользователя
     * @param dateStart начало периода
     * @param dateEnd   конец периода
     * @return Список записей в истории за период
     */
    @Query(
            value = "select"
                    + "\n h.REQUEST_HISTORY_ID"
                    + ",\n h.CREATE_TS"
                    + ",\n h.USER_ID"
                    + ",\n u.USERNAME"
                    + ",\n h.IP_ADDRESS"
                    + ",\n h.URI"
                    + ",\n h.PARAMS"
                    + ",\n h.RESULT_ID"
                    + ",\n h.RESULT_SIZE"
                    + "\n from CC_REQUESTS_HISTORY h, CC_USERS u"
                    + "\n where h.CREATE_TS >= :dateStart and h.CREATE_TS < :dateEnd and h.USER_ID = u.USER_ID"
                    + "\n order by h.CREATE_TS desc", 
            nativeQuery = true
    )
    List<RequestHistory> findAllWithRangeDesc(
            @Param("dateStart") LocalDate dateStart, 
            @Param("dateEnd")LocalDate dateEnd
    );
    
    /** 
     * Ищет все, начиная с даты + подтягивает логин пользователя
     * @param date Начало периода
     * @return Список записей с заданной даты
     */
    @Query(
            value = "select"
                    + "\n h.REQUEST_HISTORY_ID"
                    + ",\n h.CREATE_TS"
                    + ",\n h.USER_ID"
                    + ",\n u.USERNAME"
                    + ",\n h.IP_ADDRESS"
                    + ",\n h.URI"
                    + ",\n h.PARAMS"
                    + ",\n h.RESULT_ID"
                    + ",\n h.RESULT_SIZE"
                    + "\n from CC_REQUESTS_HISTORY h, CC_USERS u"
                    + "\n where h.CREATE_TS >= :date and h.USER_ID = u.USER_ID"
                    + "\n order by h.CREATE_TS", 
            nativeQuery = true
    )
    List<RequestHistory> findAllWithDateAfter(@Param("date") LocalDate date);
    
    /** 
     * Ищет все до даты по убыванию + подтягивает логин пользователя
     * @param date Конец периода
     * @return Список записей до заданной даты
     */
    @Query(
            value = "select"
                    + "\n h.REQUEST_HISTORY_ID"
                    + ",\n h.CREATE_TS"
                    + ",\n h.USER_ID"
                    + ",\n u.USERNAME"
                    + ",\n h.IP_ADDRESS"
                    + ",\n h.URI"
                    + ",\n h.PARAMS"
                    + ",\n h.RESULT_ID"
                    + ",\n h.RESULT_SIZE"
                    + "\n from CC_REQUESTS_HISTORY h, CC_USERS u"
                    + "\n where h.CREATE_TS < :date and h.USER_ID = u.USER_ID"
                    + "\n order by h.CREATE_TS desc",
            nativeQuery = true
    )
    List<RequestHistory> findAllWithDateBefore(@Param("date") LocalDate date);
    
}
