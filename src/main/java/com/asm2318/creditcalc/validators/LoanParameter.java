package com.asm2318.creditcalc.validators;

/** Ограничитель параметров кредитного калькултора*/
public class LoanParameter {
    /** Минимальная сумма кредита */
    public static final double SUM_MIN = 100000f;
    /** Максимальная сумма кредита */
    public static final double SUM_MAX = 10000000000f;
    /** Минимальный срок кредита */
    public static final int TERM_MIN = 6;
    /** Максимальный срок кредита */
    public static final int TERM_MAX = 360;
    /** Минимальная процентная ставка */
    public static final double RATE_MIN = 0.01;
    /** Максимальная процентная ставка */
    public static final double RATE_MAX = 100f;
 
    public static void validate(
            final double sum,
            final int term,
            final double rate
    ) {
        final StringBuilder errorMessage = new StringBuilder();
        if (sum < SUM_MIN)
            errorMessage.append(String.format("Минимальная сумма кредита - %.2f.\n", SUM_MIN));
        else if (sum > SUM_MAX)
            errorMessage.append(String.format("Максимальная сумма кредита - %.2f.\n", SUM_MAX));
        
        if (term < TERM_MIN)
            errorMessage.append(String.format("Минимальный срок кредита - %d мес.\n", TERM_MIN));
        else if (term > TERM_MAX)
            errorMessage.append(String.format("Максимальный срок кредита - %d мес.\n", TERM_MAX));
        
        if (rate < RATE_MIN)
            errorMessage.append(String.format("Минимальная процентная ставка - %.2f.", RATE_MIN));
        else if (rate > RATE_MAX)
            errorMessage.append(String.format("Максимальная процентная ставка - %.2f.", RATE_MAX));
        
        if (errorMessage.length() != 0)
            throw new RuntimeException(errorMessage.toString());
    }
    
}
