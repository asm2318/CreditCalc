package com.asm2318.creditcalc.dto;

/** Параметры расчета для калькулятора */
public class CalculationRequestDTO {
    
    /** Сумма кредита */
    private double sum;
    /** Процентная ставка */
    private double rate;
    /** Срок кредита */
    int term;
    /** Дата первого платежа */
    private String firstPayDate;
    /** Аннуитетный или дифференцированный */
    private boolean annuity;
    
    public double getSum(){
        return sum;
    }
    public void setSum(final double sum){
        this.sum = sum;
    }
    
    public double getRate(){
        return rate;
    }
    public void setRate(final double rate){
        this.rate = rate;
    }
    
    public boolean isAnnuity() { return annuity; }
    public void setAnnuity(final boolean annuity) { this.annuity = annuity; }
    
    public int getTerm(){
        return term;
    }
    public void setTerm(final int term){
        this.term = term;
    }
    
    public String getFirstPayDate(){
        return firstPayDate;
    }
    public void setFirstPayDate(final String firstPayDate){
        this.firstPayDate = firstPayDate;
    }
}
