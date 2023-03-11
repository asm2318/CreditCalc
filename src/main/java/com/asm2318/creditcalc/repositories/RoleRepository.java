package com.asm2318.creditcalc.repositories;

import com.asm2318.creditcalc.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, String>{
    /**
     * Ищет роль по идентификатору
     * @param roleId Идентификатор роли
     * @return Роль
     */
    Role findByRoleId(long roleId);
    
    /**
     * Ищет роль по коду
     * @param roleCode Код роли
     * @return Роль
     */
    Role findByRoleCode(String roleCode);
    
    /**
     * Обновляет роль пользователя
     * @param userId Идентификатор пользователя
     * @param roleId Идентификатор роли
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update CC_USERS_ROLES_XREF set ROLE_ID = :roleId where USER_ID = :userId", nativeQuery = true)
    void updateUserRole(@Param("userId") long userId, @Param("roleId") long roleId);
    
}
