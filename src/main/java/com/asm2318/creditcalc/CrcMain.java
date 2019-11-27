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
public class CrcMain {

    public static void main(String[] args) {
        CrcModel model = new CrcModel();
        CrcView view = new CrcView();
        CrcController controller = new CrcController(model, view);
        controller.ccFirst();
    }
    
}
