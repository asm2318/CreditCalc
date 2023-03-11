package com.asm2318.creditcalc.services;

import com.asm2318.creditcalc.rows.ScheduleRow;
import com.asm2318.creditcalc.utils.DateHelper;
import com.asm2318.creditcalc.validators.LoanParameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.time.DateUtils;

import org.apache.commons.lang3.tuple.Pair;

/** 
 * Основная бизнес-логика - кредитный калькулятор.
 * Формирует график погашения на основании вводных параметров.
 */
public class CalculatorService {
    
    /**
     * Расчет графика погашения
     * @param firstPayDate  Дата первого платежа
     * @param loan          Сумма кредита
     * @param rate          Годовая процентная ставка
     * @param annuity       Аннуитетный или дифференцированный
     * @param term          Срок в месяцах
     * @return Пара [Итоговые суммы платежей, Список платежей]
     */
    public Pair<ScheduleRow, List<ScheduleRow>> calculate(
            @Nonnull final String firstPayDate,
            final double loan,
            final double rate,
            final boolean annuity,
            final int term
    ) {
        LoanParameter.validate(loan, term, rate);
        final Date firstDate = DateHelper.formatDateFrom(firstPayDate);
        final BigDecimal loanSum = new BigDecimal(loan);
        final Pair<ScheduleRow, List<ScheduleRow>> result = annuity ? 
                calculateAnnuity(
                        firstDate,
                        loanSum,
                        rate,
                        term
                ) :
                calculateDifferent(
                        firstDate,
                        loanSum,
                        rate,
                        term
                );
        
        return checkTotals(result, loanSum, term);
    }
    
    /** 
     * Расчет аннуитетных платежей 
     * сумма ежемесячного платежа = S * (p + (p / ((1 + p)^N - 1)), где
     *      S - это сумма кредита,
     *      N - это срок в месяцах,
     *      p - это нормализованная месячная процентная ставка; p = P/100/12, где P - это годовая процентная ставка;
     * процентная составляющая платежа = p * Rem, где Rem - это остаток задолженности по кредиту;
     * сумма в погашение основного долга = ежемесячный платеж - процентная составляющая
     */
    private Pair<ScheduleRow, List<ScheduleRow>> calculateAnnuity(
            final Date firstPayDate,
            final BigDecimal loan,
            final double rate,
            final int term
    ) {
        final BigDecimal ratePerMonth = new BigDecimal(rate/100/12);
        BigDecimal rest = loan;
        final BigDecimal percents = ratePerMonth.add(
                ratePerMonth.divide(
                        ratePerMonth.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE),
                        6,
                        RoundingMode.HALF_UP
                )        
        );
        final BigDecimal paymentTotal = loan.multiply(percents).setScale(2, RoundingMode.HALF_UP);
        
        //Если процентная составляющая равна сумме основного платежа, задолженность не будет уменьшаться
        if (paymentTotal.compareTo(rest.multiply(ratePerMonth).setScale(2, RoundingMode.HALF_EVEN)) == 0)
            throw new RuntimeException("С заданными параметрами аннутитетные платежи неприменимы.");
        
        final List<ScheduleRow> scheduleRows = new ArrayList<>(term);
        final BigDecimal totalsPaymentTotal = paymentTotal.multiply(new BigDecimal(term));
        BigDecimal totalsPaymentPercent = BigDecimal.ZERO;
        BigDecimal totalsPaymentBasic = BigDecimal.ZERO;
        for (int i = 0; i < term; i++){
            final BigDecimal paymentPercent = rest.multiply(ratePerMonth).setScale(2, RoundingMode.HALF_EVEN);
            totalsPaymentPercent = totalsPaymentPercent.add(paymentPercent);
            final BigDecimal paymentBasic = paymentTotal.subtract(paymentPercent).setScale(2, RoundingMode.HALF_EVEN);
            totalsPaymentBasic = totalsPaymentBasic.add(paymentBasic);
            rest = rest.subtract(paymentBasic);
            scheduleRows.add(new ScheduleRow(
                    DateHelper.checkDay(DateUtils.addMonths(firstPayDate, i)), 
                    paymentTotal,
                    paymentPercent,
                    paymentBasic,
                    rest
            ));
        }
        return Pair.of(
                new ScheduleRow(null, totalsPaymentTotal, totalsPaymentPercent, totalsPaymentBasic, null), 
                scheduleRows
        );
    }
    
    /**
     * Расчет дифференцированных платежей - упрощенно по месяцам
     * сумма в погашение основного долга = сумма кредита / срок кредита в месяцах; далее pB;
     * сумма ежемесячного платежа = pB + Rem * p, где
     *      Rem -   это остаток долго на дату платежа;
     *      p -     это нормализованная месячная процентная ставка; p = P/100/12, где P - это годовая процентная ставка;
     * процентная составляющая платежа = сумма ежемесячного платежа - сумма в погашение основного долга
     */
    private Pair<ScheduleRow, List<ScheduleRow>> calculateDifferent(
            final Date firstPayDate,
            final BigDecimal loan,
            final double rate,
            final int term
    ){
        final BigDecimal ratePerMonth = new BigDecimal(rate/100/12);
        BigDecimal rest = loan;
        
        final List<ScheduleRow> scheduleRows = new ArrayList<>(term);
        BigDecimal totalsPaymentTotal = BigDecimal.ZERO;
        BigDecimal totalsPaymentPercent = BigDecimal.ZERO;
        
        final BigDecimal termBD = new BigDecimal(term);
        final BigDecimal paymentBasic = rest.divide(termBD, 2, RoundingMode.HALF_EVEN);
        final BigDecimal totalsPaymentBasic = paymentBasic.multiply(termBD).setScale(2, RoundingMode.HALF_EVEN);
        for (int i = 0; i < term; i++){
            final BigDecimal paymentTotal = paymentBasic.add(
                    loan.subtract(
                            paymentBasic.multiply(new BigDecimal(i))
                    ).multiply(ratePerMonth)
            ).setScale(2, RoundingMode.HALF_UP);
            totalsPaymentTotal = totalsPaymentTotal.add(paymentTotal);
            final BigDecimal paymentPercent = paymentTotal.subtract(paymentBasic).setScale(2, RoundingMode.HALF_UP);
            totalsPaymentPercent = totalsPaymentPercent.add(paymentPercent);
            rest = rest.subtract(paymentBasic);
            scheduleRows.add(new ScheduleRow(
                    DateHelper.checkDay(DateUtils.addMonths(firstPayDate, i)), 
                    paymentTotal, 
                    paymentPercent,
                    paymentBasic,
                    rest
            ));
        }
                
        return Pair.of(
                new ScheduleRow(null, totalsPaymentTotal, totalsPaymentPercent, totalsPaymentBasic, null), 
                scheduleRows
        );
    }
    
    /** Остатки округления добавляются к сумме последнего платежа */
    private Pair<ScheduleRow, List<ScheduleRow>> checkTotals(
            final Pair<ScheduleRow, List<ScheduleRow>> pair,
            final BigDecimal loan,
            final int term
    ){
        final ScheduleRow totals = pair.getLeft();
        final BigDecimal difference = loan.subtract(totals.getPaymentBasic());
        if (difference.compareTo(BigDecimal.ZERO) == 0)
            return pair;
        final ScheduleRow lastRow = pair.getRight().get(term - 1);
        lastRow.setRest(lastRow.getRest().subtract(difference));
        lastRow.setPaymentBasic(lastRow.getPaymentBasic().add(difference));
        totals.setPaymentBasic(totals.getPaymentBasic().add(difference));
        lastRow.setPaymentPercent(lastRow.getPaymentPercent().subtract(difference));
        totals.setPaymentPercent(totals.getPaymentPercent().subtract(difference));
        return pair;
    }
    
}
