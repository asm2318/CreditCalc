package com.asm2318.creditcalc.enums;

import java.util.Arrays;

/** Статус результата расчета */
public enum CalculationResult {
    SUCCESS (1, "Успешно"),
    FAILURE(2, "Не успешно"),
    ;
    
    private int id;
    
    private String description;
    
    private CalculationResult(final int id, final String description) {
        this.id = id;
        this.description = description;
    }
    
    public int getId() { return id; }
    
    public String getDescription() { return description; }
    
    public static CalculationResult parseId(final int id) {
        return Arrays.stream(CalculationResult.values()).filter(e -> e.getId() == id).findAny()
                .orElseThrow(() -> 
                        new IllegalArgumentException(String.format("Не найден тип результата с id=[%d].", id))
                );
    }
}
