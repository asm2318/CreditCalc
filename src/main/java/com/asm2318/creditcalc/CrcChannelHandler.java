/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CrcChannelHandler implements Runnable{
        private SocketChannel client;
        private String exportPath;
        
        public CrcChannelHandler(SocketChannel client, String exportPath){
            this.client = client;
            this.exportPath = exportPath;
        }
        
        public void run(){

                try{
                DataInputStream serverIn = new DataInputStream(client.socket().getInputStream());
                DataOutputStream serverOut = new DataOutputStream(client.socket().getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(serverIn));
                while(!client.socket().isClosed()){
                String header = reader.readLine();
                    if (header.contains("calculate")){
                        try{
                        String[] request = header.split(" ");
                        String pars = request[1];
                        pars = pars.substring(pars.indexOf("?")+1);
                        String[] reqPars = pars.split("&");
                        String sID = reqPars[0];
                        double sLoan = Double.valueOf(reqPars[1]);
                        double sPercent = Double.valueOf(reqPars[2]);
                        double sPayment = Double.valueOf(reqPars[3]);
                        boolean annu = reqPars[4].equals("true");
                        int sTerm = Integer.valueOf(reqPars[5]);
                        String sPaydate = reqPars[6]; 
                        CrcCalculator calculator = new CrcCalculator(sID, sLoan, sPercent, sPayment, annu, sTerm, sPaydate, exportPath, this);
                        String resPath = calculator.result();
            
                        File result = new File(resPath);
                            int answer = (int)result.length();
                            int stage = 0;
                            String response = "HTTP/1.1 200 OK\n\n";
                            byte[] byteHeader = response.getBytes();
                            serverOut.write(byteHeader,0,response.length());
                            serverOut.flush();
                            
                            while (stage == 0){
                                stage = serverIn.readInt();
                            }
                            
                        serverOut.writeInt(answer);
                        serverOut.flush();
                            while (stage == 1){
                                stage = serverIn.readInt();
                            }
                        byte[] arr = new byte[answer];
                        InputStream in = new FileInputStream(result);
                        int count;
                            while ((count = in.read(arr)) > 0) {
                            serverOut.write(arr, 0, count);
                            }
                        serverOut.flush();
                        in.close();
                        }catch(Exception e){
                            String response = "HTTP/1.1 501\n\n";
                            byte[] byteHeader = response.getBytes();
                            serverOut.write(byteHeader,0,response.length());
                            serverOut.flush();
                        }
                    }
                }
                serverIn.close();
                serverOut.close();
                client.socket().close();
                }catch(Exception e){}
                
            }
    
}
