package com.asm2318.creditcalc.utils;

import com.asm2318.creditcalc.entities.RequestHistory;
import com.asm2318.creditcalc.entities.Role;
import com.asm2318.creditcalc.entities.User;
import com.asm2318.creditcalc.rows.ScheduleRow;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;

/** Преобразование в json */
public class JsonConverter {
    
    public static String convertPaymentSchedule(
            @Nonnull final Pair<ScheduleRow, List<ScheduleRow>> scheduleRows,
            @Nonnull final String firstPayDate
    ) {
        final StringBuilder stringBuilder = new StringBuilder("{");
        appendSchedule(stringBuilder, scheduleRows.getRight());
        stringBuilder.append(",");
        appendTotals(stringBuilder, scheduleRows.getLeft());
        stringBuilder.append(",")
                .append(jsonKeyValue(FieldName.CALCULATOR_DATE, firstPayDate))
                .append("}");
        return stringBuilder.toString();
    }
    
    private static void appendSchedule(
            @Nonnull final StringBuilder stringBuilder,
            @Nonnull final List<ScheduleRow> rows
    ) {
        stringBuilder
                .append(String.format("\"%s\":[", FieldName.CALCULATOR_RESULT_SCHEDULE))
                .append(
                    rows.stream().map(r ->  String.format(
                                "{%s,%s,%s,%s,%s}",
                                jsonKeyValue("payDate", DateHelper.formatDateTo(r.getDate())),
                                jsonKeyValue("paymentTotal", r.getPaymentTotal().toString()),
                                jsonKeyValue("paymentPercent", r.getPaymentPercent().toString()),
                                jsonKeyValue("paymentBasic", r.getPaymentBasic().toString()),
                                jsonKeyValue("rest", r.getRest().toString())
                    )).collect(Collectors.joining(","))
                )
                .append("]");
    }
    
    private static void appendTotals(
            @Nonnull final StringBuilder stringBuilder,
            @Nonnull final ScheduleRow row
    ) {
        stringBuilder.append(String.format(
                "\"%s\":{%s,%s,%s}",
                FieldName.CALCULATOR_RESULT_TOTALS,
                jsonKeyValue("totalsPaymentTotal", row.getPaymentTotal().toString()),
                jsonKeyValue("totalsPaymentPercent", row.getPaymentPercent().toString()),
                jsonKeyValue("totalsPaymentBasic", row.getPaymentBasic().toString())
        ));
    }
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    
    public static String convertRequestsHistory(@Nonnull final List<RequestHistory> requestsHistory){
        return String.format(
                "[%s]",
                requestsHistory.stream().map(h -> 
                    String.format(
                            "{%s,%s,%s,%s,%s,%s,%s,%s,%s}",
                            jsonKeyValue("requestHistoryId", String.valueOf(h.getRequestHistoryId())),
                            jsonKeyValue("createTs", h.getCreateTs().format(DATE_TIME_FORMATTER)),
                            jsonKeyValue("userId", String.valueOf(h.getUserId())),
                            jsonKeyValue("username", h.getUsername()),
                            jsonKeyValue("ipAddress", h.getIpAddress()),
                            jsonKeyValue("uri", h.getUri()),
                            jsonKeyValue("params", h.getParams()),
                            jsonKeyValue("resultName", Objects.requireNonNull(h.getResult()).getDescription()),
                            jsonKeyValue("resultSize", String.valueOf(h.getResultSize()))
                    )
                ).collect(Collectors.joining(","))
        );
    }
    
    public static String convertUsersList(@Nonnull final List<User> users){
        return String.format(
                "[%s]",
                users.stream().map(u ->  
                    String.format(
                            "{%s,%s,%s,%s,%s}",
                            jsonKeyValue("userId", String.valueOf(u.getUserId())),
                            jsonKeyValue("username", u.getUsername()),
                            jsonKeyValue("email", u.getEmail()),
                            jsonKeyValue("status", u.isEnabled() ? "Активен" : "Не активен"),
                            jsonKeyValue("roleId", String.valueOf(u.getRole().getRoleId()))
                    )
                ).collect(Collectors.joining(","))
        );
    }
    
    public static String convertRolesList(@Nonnull final List<Role> roles) {
        return String.format(
                "[%s]",
                roles.stream().map(r ->  
                    String.format(
                            "{%s,%s}",
                            jsonKeyValue("roleId", String.valueOf(r.getRoleId())),
                            jsonKeyValue("roleName", r.getRoleName())
                    )
                ).collect(Collectors.joining(","))
        );
    } 
    
    private static String jsonKeyValue(@Nonnull final String key, @Nonnull final String value) {
        return String.format("\"%s\":\"%s\"", key, value);
    }
}
