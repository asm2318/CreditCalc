package com.asm2318.creditcalc.rows;

import java.math.BigDecimal;
import java.util.Date;

/** Строка графика погашения */
public class ScheduleRow {
    /** Дата платежа */
    private Date date;
    /** Сумма платежа - общая */
    private BigDecimal paymentTotal;
    /** Сумма платежа по процентам */
    private BigDecimal paymentPercent;
    /** Сумма основного платежа*/
    private BigDecimal paymentBasic;
    /** Остаток платежа */
    private BigDecimal rest;
    
    public ScheduleRow(){}
    
    public ScheduleRow(
        final Date date,
        final BigDecimal paymentTotal,
        final BigDecimal paymentPercent,
        final BigDecimal paymentBasic,
        final BigDecimal rest
    ) {
        this.date = date;
        this.paymentTotal = paymentTotal;
        this.paymentPercent = paymentPercent;
        this.paymentBasic = paymentBasic;
        this.rest = rest;
    }
    
    public void setDate(final Date date) { this.date = date; }
    
    public Date getDate() { return date; }
    
    public void setPaymentTotal(final BigDecimal paymentTotal) {
        this.paymentTotal = paymentTotal;
    }
    
    public BigDecimal getPaymentTotal() {return paymentTotal; }
    
    public void setPaymentPercent(final BigDecimal paymentPercent) {
        this.paymentPercent = paymentPercent;
    }
    
    public BigDecimal getPaymentPercent() { return paymentPercent; }
    
    public void setPaymentBasic(final BigDecimal paymentBasic) {
        this.paymentBasic = paymentBasic;
    }
    
    public BigDecimal getPaymentBasic() { return paymentBasic; }
    
    public void setRest(final BigDecimal rest) { this.rest = rest; }
    
    public BigDecimal getRest() { return rest; }
    
}
