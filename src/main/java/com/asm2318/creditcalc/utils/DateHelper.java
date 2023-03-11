package com.asm2318.creditcalc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Nonnull;

/** Вспомогательный класс для работы с датами */
public class DateHelper {
    
    private static final SimpleDateFormat DATE_FORMAT_TO = new SimpleDateFormat("dd.MM.yyyy");
    
    private static final SimpleDateFormat DATE_FORMAT_FROM = new SimpleDateFormat("yyyy-MM-dd");
    
    public static Date formatDateFrom(final String stringDate) {
        try {
            return DATE_FORMAT_FROM.parse(stringDate);
        } catch (final ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String reformatDate(final String stringDate) {
        try {
            return DATE_FORMAT_TO.format(DATE_FORMAT_FROM.parse(stringDate));
        } catch (final ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String formatDateTo(@Nonnull final Date date) {
        return DATE_FORMAT_TO.format(date);
    }
    
    /** Если дата выпадает на субботу или воскресенье, платеж переносится на следующий понедельник. */
    public static Date checkDay(@Nonnull final Date date){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            calendar.add(Calendar.DATE, 2);
            return calendar.getTime();
        }else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            calendar.add(Calendar.DATE, 1);
            return calendar.getTime();
        }
        return date;
    }
    
}
