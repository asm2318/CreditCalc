/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Алексей
 */
public class CrcModel {
    //private static final int LIMIT = 4;
    static String importPath, exportPath, serialPath;
    String sep = "\\";
    final String newline = System.getProperty("line.separator");
    final int nl = newline.length();
    int result, lost, lostLines;
    CopyOnWriteArrayList<String> listID, listPaydate, listLoan, listPercent, listPayment, listTerm, listAnnu;
    CopyOnWriteArrayList<CopyOnWriteArrayList> lists;
    File importDir, exportDir, serializeDir;
    CrcController controller;
    int port;
    String ipAddress;

    public void startWork(CrcController controller){
        lost = 0;
        lostLines = 0;
        result = 0;
        this.controller = controller;
        
        //for jar
        /*sep = System.getProperty("file.separator");
        try{
        importPath = getPath() + sep + "_import" + sep;
        exportPath = getPath() + sep + "_export" + sep;
        serialPath = getPath() + sep + "_serialized" + sep;
        }catch (Exception e){
        System.out.println("ERROR: "+e);
        }*/
        //for jar
        //for ide
        importPath = ".\\_import\\";
        exportPath = ".\\_received\\";
        serialPath = ".\\_serialized\\";
        
        //for ide
        
        importDir = new File(importPath);
        exportDir = new File(exportPath);
        serializeDir = new File(serialPath);
        
        checkFolders(importDir, exportDir, serializeDir);
        importer(importDir);
    }
    
    public void setPort(int port, String ipAddress){
        this.port = port;
        this.ipAddress = ipAddress;
    }
    public String getPath() throws UnsupportedEncodingException{
        URL url = CrcModel.class.getProtectionDomain().getCodeSource().getLocation();
        String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
        String parentPath = new File(jarPath).getParentFile().getPath();
        return parentPath;
    }
    public int getResult(){
        return result;
    }
    public int getLost(){
        return lost;
    }
    public int getLostLines(){
        return lostLines;
    }
    
    private void checkFolders(File in, File out, File s){
        
        if (!in.exists()){
            in.mkdir();
            result=1;
        }
        if (!out.exists()){
            out.mkdir();
        }
        if (!s.exists()){
            s.mkdir();
        }
    }
    
    private void importer(File folder){
        listID = new CopyOnWriteArrayList<String>();
        listPaydate = new CopyOnWriteArrayList<String>();
        listLoan = new CopyOnWriteArrayList<String>();
        listPercent = new CopyOnWriteArrayList<String>();
        listPayment = new CopyOnWriteArrayList<String>();
        listTerm = new CopyOnWriteArrayList<String>();
        listAnnu = new CopyOnWriteArrayList<String>();
        lists = new CopyOnWriteArrayList<CopyOnWriteArrayList>();
        lists.add(listID);
        lists.add(listLoan);
        lists.add(listPercent);
        lists.add(listPayment);
        lists.add(listAnnu);
        lists.add(listTerm);
        lists.add(listPaydate);
        int filesnum = 0;
        String line = "";
        String value = "";
        String text = "";
        
        for (File f:folder.listFiles()){
            filesnum++;
            String fname = f.getName();
            if (fname.endsWith(".csv")){
                try{
                    /*DataInputStream stream = new DataInputStream(new BufferedInputStream (new FileInputStream(f)));
                    while(stream.available()!=0){
                        line = stream.readLine();
                        for (int i=0; i<lists.size(); i++){
                            int n = line.indexOf(";");
                                if (n!=-1){
                                value = line.substring(0,n);
                                line = line.substring(n+1);
                                    }else{
                                    value = line;
                                }
                            listCurr = lists.get(i);
                            listCurr.add(value);
                        }
                    }*/
                    BufferedInputStream stream = new BufferedInputStream (new FileInputStream(f));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int r = 0;
                    while((r = stream.read())!=-1){
                        out.write((byte)r);
                    }
                    text = out.toString();
                    CopyOnWriteArrayList<Integer> lines = new CopyOnWriteArrayList<Integer>();
                    if (text.contains(newline)){
                        for(int i=0; i<text.length(); i++){
                            if (text.charAt(i)==newline.charAt(0)) lines.add(i);
                        }
                        int g=0;
                        for(int x=0; x<lines.size(); x++){
                            int n = lines.get(x);
                            line = text.substring(g,n);
                            g=n+nl;
                            fillLists(line);
                        }
                        
                        line = text.substring(g);
                        if (!line.isEmpty()){
                        fillLists(line);
                        }
                    }else{
                        line=text;
                        fillLists(line);
                    }
                                        
                }catch (Exception e){
                    lost++;
                    result = 2;
                    System.out.println(e);
                }
            }else{
                lost++;
                result = 2;
            }
        }
        
        if (filesnum ==0){ result=1;}
        else{
           readLists(true, lists); 
        }
    }
    private void fillLists(String line){
        CopyOnWriteArrayList<String> listCurr;
        String value="";
        for (int i=0; i<lists.size(); i++){
            int n = line.indexOf(";");
                if (n!=-1){
                    value = line.substring(0,n);
                    line = line.substring(n+1);
                }else{
                    value = line;
                }
            listCurr = lists.get(i);
            listCurr.add(value);
        }
                        
    }
    
    
    private void readLists(boolean half, CopyOnWriteArrayList<CopyOnWriteArrayList> object){
        listID = object.get(0);
        listLoan = object.get(1);
        listPercent = object.get(2);
        listPayment = object.get(3);
        listAnnu = object.get(4);
        listTerm = object.get(5);
        listPaydate = object.get(6);
        
        if (listID.size()>0){
            int listsize = listID.size();
            int j = 0;
            if (listID.size()>1){
            j = (int)Math.round(listID.size()/2);
            if (half){
                listsize = (int)Math.round(listID.size()/2);
                j = 0;
            }
            }
           //ExecutorService executor = Executors.newFixedThreadPool(LIMIT); 
            try{
               SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
               DataInputStream clientIn = new DataInputStream(socket.socket().getInputStream());
                DataOutputStream clientOut = new DataOutputStream(socket.socket().getOutputStream());
               for (int i=j; i<listsize; i++){
                   String sID = listID.get(i);
                   String parameters = sID+"&"+listLoan.get(i)+"&"+listPercent.get(i)+
                "&"+listPayment.get(i)+"&"+listAnnu.get(i)+"&"+listTerm.get(i)+"&"+listPaydate.get(i);
                String request = "GET /calculate?"+parameters+" HTTP/1.1\r\n\r\n";
                /*byte[] byteHeader = request.getBytes("UTF-8");
                ByteBuffer buffer = ByteBuffer.wrap(byteHeader);
                socket.write(buffer);
                int stage = 0;
                    
                    String header = "";
                    while (stage == 0){
                        int bytesRead = socket.read(buffer);
                        header = new String(buffer.array(), "UTF-8");
                        stage = 1;
                        socket.writeInt(stage);
                    }*/
                byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
                    int stage = 0;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
                    String header = "";
                    while (stage == 0){
                        header = reader.readLine();
                        stage = 1;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                    }
                    //System.out.println(header);
                    if(header.contains("200")){
                        int answer = 0;
                        while(stage == 1){
                        answer = clientIn.readInt();
                        stage=2;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                        }
                        String fpath = exportPath+sID+".json";
                            try {
                                FileOutputStream out = new FileOutputStream(fpath);
                                byte[] arr = new byte[answer];
                                int count;
                                while (answer>0 && (count = clientIn.read(arr)) > 0) {
                                    out.write(arr, 0, count);
                                    answer -= count;
                                }
                                out.close();

                            }catch (Exception e){}
                
                        File result = new File(fpath);
                        if(result.exists()){controller.ccFile(result);}
                    }else{
                        if (result == 2){result = 4;
                        }else{result=3;}
                        lostLines++;
                        //System.out.println(header);
                    }
               }
            /*Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress,port), 5000);
            //System.out.println("Connect...");
            DataInputStream clientIn = new DataInputStream(socket.getInputStream());
            //ObjectOutputStream clientOut = new ObjectOutputStream(socket.getOutputStream());
            DataOutputStream clientOut = new DataOutputStream(socket.getOutputStream());
            for (int i=j; i<listsize; i++){
                //HTTP socket
                String sID = listID.get(i);
                String parameters = sID+"&"+listLoan.get(i)+"&"+listPercent.get(i)+
                "&"+listPayment.get(i)+"&"+listAnnu.get(i)+"&"+listTerm.get(i)+"&"+listPaydate.get(i);
                String request = "GET calculate?"+parameters+" HTTP/1.1\n\n";
                byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
                    int stage = 0;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
                    String header = "";
                    while (stage == 0){
                        header = reader.readLine();
                        stage = 1;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                    }
                    //System.out.println(header);
                    if(header.contains("200")){
                        int answer = 0;
                        while(stage == 1){
                        answer = clientIn.readInt();
                        stage=2;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                        }
                        String fpath = exportPath+sID+".json";
                            try {
                                FileOutputStream out = new FileOutputStream(fpath);
                                byte[] arr = new byte[answer];
                                int count;
                                while (answer>0 && (count = clientIn.read(arr)) > 0) {
                                    out.write(arr, 0, count);
                                    answer -= count;
                                }
                                out.close();

                            }catch (Exception e){}
                
                        File result = new File(fpath);
                        if(result.exists()){controller.ccFile(result);}
                    }else{
                        if (result == 2){result = 4;
                        }else{result=3;}
                        lostLines++;
                        //System.out.println(header);
                    }
                    
                
                /*without HTTP
                String sID = listID.get(i);
                double sLoan = Double.valueOf(listLoan.get(i));
                double sPercent = Double.valueOf(listPercent.get(i));
                double sPayment = Double.valueOf(listPayment.get(i));
                boolean annu = false;
                String sAnnu = listAnnu.get(i);
                if (sAnnu.equals("annu")){
                    annu = true;
                }
                int sTerm = Integer.valueOf(listTerm.get(i));
                String sPaydate = listPaydate.get(i);
                ArrayList<Object> lineToSend = new ArrayList<Object>();
                lineToSend.add(sID);
                lineToSend.add(sLoan);
                lineToSend.add(sPercent);
                lineToSend.add(sPayment);
                lineToSend.add(annu);
                lineToSend.add(sTerm);
                lineToSend.add(sPaydate);
                clientOut.writeObject(lineToSend);
                clientOut.flush();
                //String answer = clientIn.readUTF();
                //System.out.println("Server received ID: "+answer);
                //Runnable calculator = new CrcCalculator(sID, sLoan, sPercent, sPayment, annu, sTerm, sPaydate, exportPath);
                //executor.execute(calculator); 
                
                boolean gotSize = false;
                int answer = 0;
                while (!gotSize){
                    answer = clientIn.readInt();
                    gotSize=true;
                }
                String fpath = exportPath+sID+".json";
                try {
                    FileOutputStream out = new FileOutputStream(fpath);
                    byte[] arr = new byte[answer];
                    int count;
                    while (answer>0 && (count = clientIn.read(arr)) > 0) {
                        out.write(arr, 0, count);
                        answer -= count;
                    }
                    out.close();
                }catch (Exception e){}
                
                File result = new File(fpath);
                if(result.exists()){controller.ccFile(result);}*
            }*/
            if (half){
               serialize(lists);
               deserialize();
            }
           }catch (Exception e){
               System.out.println("client: "+e);
           }finally{
                /*executor.shutdown();
                try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {}*/
           }
        }
    }
    
    private void serialize(CopyOnWriteArrayList<CopyOnWriteArrayList> object){
        try{
        FileOutputStream fos = new FileOutputStream(serialPath+"list.serial");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        }catch (Exception e){}
    }
    
    
    private void deserialize(){
        try{
        FileInputStream fis = new FileInputStream(serialPath+"list.serial");
        ObjectInputStream ois = new ObjectInputStream(fis);
        CopyOnWriteArrayList<CopyOnWriteArrayList> object = (CopyOnWriteArrayList)ois.readObject();
        ois.close();
        fis.close();
        readLists(false, object);
        }catch (Exception e){}
    }
}
