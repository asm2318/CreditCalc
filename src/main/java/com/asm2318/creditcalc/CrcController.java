/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.File;
import java.net.Socket;
import java.util.Scanner;

public class CrcController {
    private CrcModel model;
    private CrcView view;
    private CrcServer server;
    
    public CrcController(CrcModel model, CrcView view, CrcServer server){
        this.model = model;
        this.view = view;
        this.server = server;
    }

    public void ccFirst(){
        view.typeFirst();
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        if (line != null) ccStart();
    }
    
    public void ccStart(){
        view.typeStart();
        model.startWork(this);
        ccFinish();
    }
    public void ccFinish(){
        int code = model.getResult();
        int lost = model.getLost();
        int lostLines = model.getLostLines();
        view.printResult(code, lost, lostLines);
        if (code==1){
            ccFirst();
        }else{
            try{
                server.fin = true;
                //server.serverSocket.close();
                server.serverSocket.socket().close();
            }catch (Exception e){}        
        }
    }
    
    public void ccFile(File file){
        view.fileLoad(file.getName());
    }
}
