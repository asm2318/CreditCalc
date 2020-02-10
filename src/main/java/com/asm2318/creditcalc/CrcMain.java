/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CrcMain {
    /*non web
    static int port=8080;
    static String ipAddress="127.0.0.1";
    public static void main(String[] args) {
        CrcServer server = new CrcServer(port);
        Thread thread = new Thread(server);
        thread.start();
        CrcModel model = new CrcModel();
        model.setPort(port, ipAddress);
        CrcView view = new CrcView();
        CrcController controller = new CrcController(model, view, server);
        controller.ccFirst();
    }  */
    public static void main(String[] args) {
        SpringApplication.run(CrcMain.class, args);
        /*String password = new BCryptPasswordEncoder().encode("123");
        System.out.println("PSW: "+password);*/
    }
    
}
