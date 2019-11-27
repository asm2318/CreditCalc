/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.util.Scanner;

/**
 *
 * @author Алексей
 */
public class CrcController {
    private CrcModel model;
    private CrcView view;
    
    public CrcController(CrcModel model, CrcView view){
        this.model = model;
        this.view = view;
    }

    public void ccFirst(){
        view.typeFirst();
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        if (line != null) ccStart();
    }
    
    public void ccStart(){
        view.typeStart();
        model.startWork();
        ccFinish();
    }
    public void ccFinish(){
        int code = model.getResult();
        int lost = model.getLost();
        view.printResult(code, lost);
        if (code==1){
            ccFirst();
        }
    }
}
