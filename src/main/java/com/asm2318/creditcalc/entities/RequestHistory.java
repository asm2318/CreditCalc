package com.asm2318.creditcalc.entities;

import com.asm2318.creditcalc.enums.CalculationResult;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

/** Строка истории рассчетов калькулятора */
@Entity
@Table(name="CC_REQUESTS_HISTORY")
public class RequestHistory implements Serializable {
    
    /** Идентификатор строки истории */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestHistoryId;
    /** Дата и время создания */
    private LocalDateTime createTs;
    /** Идентификатор пользователя */
    @Column(name = "USER_ID")
    private long userId;
    /** Логин пользователя - для отображения на клиенте*/
    @Column(insertable=false, updatable=false)
    private String username;
    /** ip-адрес запроса */
    private String ipAddress;
    /** Uri, в рамках задания всегда один и тот же */
    private String uri;
    /** Входные параметры */
    private String params;
    /** Идентификатор статуса результата */
    @Basic
    private int resultId;
    
    /** Статус результата - для отображения на клиенте */
    @Transient
    private CalculationResult result;
    @PostLoad
    void fillTransient() {
        if (resultId > 0) this.result = CalculationResult.parseId(resultId);
    }
    @PrePersist
    void fillPersistent() {
        if (result != null) this.resultId = result.getId();
    }
    
    /** Размер результата в формате json в байтах */
    private int resultSize;

    public RequestHistory(){}
    
    public RequestHistory(
            final LocalDateTime createTs,
            final long userId,
            final String ipAddress, 
            final String uri, 
            final String params, 
            final int resultId, 
            final int resultsize
    ) {
        this.createTs = createTs;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.uri = uri;
        this.params = params;
        this.result = CalculationResult.parseId(resultId);
        this.resultSize = resultsize;
    }
    
    public Long getRequestHistoryId() {
        return requestHistoryId;
    }
    
    public LocalDateTime getCreateTs() {
        return createTs;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public String getUri() {
        return uri;
    }
    
    public String getParams() {
        return params;
    }
    
    public CalculationResult getResult() {
        return result;
    }
    
    public int getResultSize() {
        return resultSize;
    }

}
