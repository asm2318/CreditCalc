package com.asm2318.creditcalc.services;

import com.asm2318.creditcalc.entities.RequestHistory;
import com.asm2318.creditcalc.entities.Role;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.repositories.UserRepository;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.asm2318.creditcalc.repositories.RequestHistoryRepository;
import com.asm2318.creditcalc.repositories.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdministratorServiceImpl implements AdministratorService {
    
    @Autowired
    private RequestHistoryRepository requestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    
    @Override
    public List<RequestHistory> getRequestsHistory(@Nullable final String dateStart, @Nullable final String dateEnd){
        final LocalDate localDateStart = StringUtils.isNotBlank(dateStart) ? LocalDate.parse(dateStart) : null;
        final LocalDate localDateEnd = StringUtils.isNotBlank(dateEnd) ? LocalDate.parse(dateEnd) : null;

        if (localDateStart == null && localDateEnd == null){
            return requestRepository.findAllByOrderByCreateTs();
        }
        if (localDateStart != null && localDateEnd == null){
            return requestRepository.findAllWithDateAfter(localDateStart);
        }
        if (localDateStart == null && localDateEnd != null){
            return requestRepository.findAllWithDateBefore(localDateEnd.plusDays(1L));
        }
        if (localDateEnd.compareTo(localDateStart) < 0) {
            return requestRepository.findAllWithRangeDesc(localDateEnd, localDateStart.plusDays(1L));
        }
        return requestRepository.findAllWithRange(localDateStart, localDateEnd.plusDays(1L));
    }
    
    @Override
    public List<User> getUsersList() {
        return userRepository.findAllByOrderByUsername();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(@Nullable final Long userId, @Nullable final Long roleId) {
        if (userId == null) throw new IllegalArgumentException("Не передан идентификатор пользователя.");
        if (roleId == null) throw new IllegalArgumentException("Не передан идентификатор новой роли.");
        
        final User lockedUser = userRepository.findByUserId(userId).orElseThrow(() -> 
                new RuntimeException(String.format(
                        "Не найден пользователь с id=[%d]",
                        userId
                ))
        );
        roleRepository.updateUserRole(lockedUser.getUserId(), roleId);
    }
    
    @Override
    public List<Role> getRolesList() {
        return roleRepository.findAll();
    }
    
}
