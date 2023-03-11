package com.asm2318.creditcalc.dto;

/** Параметры запроса от администратора */
public class AdministratorRequestDTO {
    
    /** Идентификатор пользователя */
    private Long userId;
    /** Идентификатор новой роли */
    private Long updateRoleId;
    /** Дата начала периода */
    private String dateStart;
    /** Дата окончания периода */
    private String dateEnd;
    
    public Long getUserId() { return userId; }
    
    public void setUserId(final Long userId) { this.userId = userId; }
    
    public Long getUpdateRoleId() { return updateRoleId; }
    
    public void setUpdateRoleId(final Long updateRoleId) { this.updateRoleId = updateRoleId; }
    
    public String getDateStart() { return dateStart; }
    
    public void setDateStart(final String dateStart) { this.dateStart = dateStart; }
    
    public String getDateEnd() { return dateEnd; }
    
    public void setDateEnd(final String dateEnd) { this.dateEnd = dateEnd; }
    
}
