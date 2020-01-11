/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

/**
 *
 * @author Алексей
 */
public class CrcView {
    public void typeFirst(){
        System.out.println("Нажмите Enter, чтобы начать:");
    }
    
    public void typeStart(){
        System.out.println("Формирование графиков...");
    }
    
    public void printResult(int code, int lost, int lostLines){
        if (code==0){
        System.out.println("Формирование графиков завершено");
        } else if (code == 1){
        System.out.println("Файлы для расчета не найдены. Разместите их в каталог \"_import\" и повторите процедуру.");
        } else if (code == 2){
        System.out.println("В каталоге \"_import\" остались файлы, которые не удалось обработать. Количество: " + lost);
        } else if (code == 3){
        System.out.println("Не удалось обработать отдельные записи. Количество: " + lostLines);  
        } else if (code == 4){
        System.out.println("Не удалось обработать файлы. Количество: " + lost +". Не удалось обработать отдельные записи. Количество: " + lostLines); 
        }
    }
    
    public void fileLoad(String file){
        System.out.println("Файл \""+file+"\" успешно сохранен.");
    }
}
