package com.asm2318.creditcalc;

import com.asm2318.creditcalc.rows.ScheduleRow;
import com.asm2318.creditcalc.services.CalculatorService;
import com.asm2318.creditcalc.validators.LoanParameter;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import org.junit.jupiter.api.Test;;

public class CalculatorServiceTest {
    
    private void checkResult(
            final ScheduleRow totals, 
            final List<ScheduleRow> rows, 
            final double loan, 
            final int term
    ) {
        assertEquals(
                totals.getPaymentBasic(),
                rows.stream().map(ScheduleRow::getPaymentBasic).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        assertEquals(totals.getPaymentBasic().compareTo(new BigDecimal(loan)), 0);
        
        assertEquals(
                totals.getPaymentPercent(),
                rows.stream().map(ScheduleRow::getPaymentPercent).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        assertEquals(
                totals.getPaymentTotal(),
                rows.stream().map(ScheduleRow::getPaymentTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        assertEquals(
                totals.getPaymentTotal(),
                totals.getPaymentBasic().add(totals.getPaymentPercent())
        );
        
        assertEquals(rows.get(rows.size() - 1).getRest().compareTo(BigDecimal.ZERO), 0);
        
        assertFalse(
                rows.stream().filter(r -> {
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(r.getDate());
                    final int day = calendar.get(Calendar.DAY_OF_WEEK);
                    return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
                }).findAny().isPresent()
        );
        
        assertEquals(rows.size(), term);
    }
    
    /** Расчет аннуитетных платежей со средними параметрами */
    @Test
    public void averageAnnuityTest() {
        final double loan = 100000;
        final int term = 12;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                12,
                true,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет аннуитетных платежей с минимальными параметрами */
    @Test
    public void minAnnuityTest() {
        final double loan = LoanParameter.SUM_MIN;
        final int term = LoanParameter.TERM_MIN;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                LoanParameter.RATE_MIN,
                true,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет аннуитетных платежей с максимальными параметрами - должен выдавать ошибку */
    @Test
    public void maxAnnuityTest() {
        final double loan = LoanParameter.SUM_MAX;
        final int term = LoanParameter.TERM_MAX;
        assertThrows(
                RuntimeException.class,
                () -> { new CalculatorService().calculate(
                        "2020-01-01",
                        loan,
                        LoanParameter.RATE_MAX,
                        true,
                        term
                );}
        );
    }
    
    /** Расчет аннуитетных платежей с большими параметрами */
    @Test
    public void largeAnnuityTest() {
        final double loan = 100000000;
        final int term = LoanParameter.TERM_MAX;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                20,
                true,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет дифференцированных платежей со средними параметрами */
    @Test
    public void averageDifferenceTest() {
        final double loan = 100000;
        final int term = 12;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                12,
                false,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет дифференцированных платежей с минимальными параметрами */
    @Test
    public void minDifferenceTest() {
        final double loan = LoanParameter.SUM_MIN;
        final int term = LoanParameter.TERM_MIN;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                LoanParameter.RATE_MIN,
                false,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет дифференцированных платежей с максимальными параметрами */
    @Test
    public void maxDifferenceTest() {
        final double loan = LoanParameter.SUM_MAX;
        final int term = LoanParameter.TERM_MAX;
        final Pair<ScheduleRow, List<ScheduleRow>> resultPair = new CalculatorService().calculate(
                "2020-01-01",
                loan,
                LoanParameter.RATE_MAX,
                false,
                term
        );
        checkResult(resultPair.getLeft(), resultPair.getRight(), loan, term);
    }
    
    /** Расчет аннуитетных платежей со средними параметрами - 1 млн. за минуту */
    @Test
    public void timeoutAnnuityTest() {
        assertTimeout(
                Duration.ofMinutes(1),
                () -> {
                    for (int x = 0; x < 1000000; x++) {
                        new CalculatorService().calculate(
                                "2020-01-01",
                                100000,
                                12,
                                true,
                                12
                        );
                    }
                }
        );
    }
    
    /** Расчет дифференцированных платежей со средними параметрами - 1 млн. за минуту */
    @Test
    public void timeoutDifferenceTest() {
        assertTimeout(
                Duration.ofMinutes(1),
                () -> {
                    for (int x = 0; x < 1000000; x++) {
                        new CalculatorService().calculate(
                                "2020-01-01",
                                100000,
                                12,
                                false,
                                12
                        );
                    }
                }
        );
    }
}
