/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import static com.asm2318.creditcalc.CrcModel.exportPath;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CrcServer implements Runnable{
    File exportDir;
    String exportPath;
    int port;
    ExecutorService executor;
    ServerSocketChannel serverSocket;
    private static final int LIMIT = 4;
    boolean fin;
    public CrcServer(int port){
        this.port = port;
        fin = false;
        exportPath = ".\\_export\\";
        exportDir = new File(exportPath);
        checkFolders(exportDir);
    }    
    
    public void run(){
        
        executor = Executors.newFixedThreadPool(LIMIT); 
        try{
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
            while(!serverSocket.socket().isClosed()){
                SocketChannel socket = serverSocket.accept();
                socket.socket().setSoTimeout(100);
                executor.execute(new CrcChannelHandler(socket, exportPath)); 
            }
        }catch (Exception e){
            if(!fin){e.printStackTrace();}
        }finally{
            executor.shutdown();
            //System.out.println("Server closed");
            System.exit(0);
        }
    }
    
    private void checkFolders(File exp){
        if (!exp.exists()){
            exp.mkdir();
        }
    }
        
}
