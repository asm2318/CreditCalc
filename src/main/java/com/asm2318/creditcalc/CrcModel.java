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
import java.io.FileReader;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    ArrayList<String> listID, listPaydate, listLoan, listPercent, listPayment, listTerm, listAnnu, listCurr;
    ArrayList<ArrayList> lists;
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
    
    public void importer(File folder){
        listID = new ArrayList<String>();
        listPaydate = new ArrayList<String>();
        listLoan = new ArrayList<String>();
        listPercent = new ArrayList<String>();
        listPayment = new ArrayList<String>();
        listTerm = new ArrayList<String>();
        listAnnu = new ArrayList<String>();
        lists = new ArrayList<ArrayList>();
        lists.add(listID);
        lists.add(listLoan);
        lists.add(listPercent);
        lists.add(listPayment);
        lists.add(listAnnu);
        lists.add(listTerm);
        lists.add(listPaydate);
        int filesnum = 0;
        String line = "";
        String text = "";
        
        for (File f:folder.listFiles()){
            filesnum++;
            String fname = f.getName();
            if (fname.endsWith(".csv")){
                try{
                    BufferedInputStream stream = new BufferedInputStream (new FileInputStream(f));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int r = 0;
                    while((r = stream.read())!=-1){
                        out.write((byte)r);
                    }
                    text = out.toString();
                    ArrayList<Integer> lines = new ArrayList<Integer>();
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
        
        String value="";
        if(!line.isEmpty() && !line.equals("")){
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
    }
    
    
    public void readLists(boolean half, ArrayList<ArrayList> object){
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
                //System.out.println("client sends request: "+request);
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
                        if(result.exists()){
                            controller.ccFile(result);
                            //parseJson(result);
                        }
                    }else{
                        if (result == 2){result = 4;
                        }else{result=3;}
                        lostLines++;
                        //System.out.println(header);
                    }
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
                if(result.exists()){controller.ccFile(result);}*/

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
    
    public void serialize(ArrayList<ArrayList> object){
        try{
        FileOutputStream fos = new FileOutputStream(serialPath+"list.serial");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        }catch (Exception e){}
    }
    
    
    public void deserialize(){
        try{
        FileInputStream fis = new FileInputStream(serialPath+"list.serial");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList> object = (ArrayList)ois.readObject();
        ois.close();
        fis.close();
        readLists(false, object);
        }catch (Exception e){}
    }
    
    public void parseJson(File file){

        ArrayList<Object> resultList = new ArrayList<Object>();
        try{
        Object object = new JSONParser().parse(new FileReader(file));
        JSONObject json = (JSONObject)object;
        String sID = (String)json.get("id");
        double sLoan = (Double)json.get("loan");
        double sRate = (Double)json.get("rate");
        double sFirstPay = (Double)json.get("firstpay");
        String sAnnuity = (String)json.get("annuity");
        long sTerm = (Long)json.get("term");
        String sPayDate = (String)json.get("firstpaydate");
        //System.out.println("NEW CALCULATOR: "+sID+" / Loan: "+sLoan+" / Rate: "+sRate+" / FirstPay: "+sFirstPay+" / Annuity: "+sAnnuity+" / Term: "+sTerm+" / First date: "+sPayDate);
        ArrayList<String> sDates = new ArrayList<String>();
        ArrayList<Double> sSumPay = new ArrayList<Double>();
        ArrayList<Double> sPerPay = new ArrayList<Double>();
        ArrayList<Double> sBasPay = new ArrayList<Double>();
        ArrayList<Double> sRest = new ArrayList<Double>();
        JSONArray shedule = (JSONArray) json.get("shedule");
        Iterator itrtr = shedule.iterator();
            while(itrtr.hasNext()){
                JSONObject shdlObj = (JSONObject)itrtr.next();
                String tDate = (String)shdlObj.get("date");
                sDates.add(tDate);
                double tSum = (Double)shdlObj.get("summ_pay");
                sSumPay.add(tSum);
                double tPer = (Double)shdlObj.get("percent_pay");
                sPerPay.add(tPer);
                double tBas = (Double)shdlObj.get("basic_pay");
                sBasPay.add(tBas);
                double tRest = (Double)shdlObj.get("rest");
                sRest.add(tRest);
                //System.out.println(tDate+" / "+tSum+" / "+tPer+" / "+tBas+" / "+tRest);
            }
        JSONObject totals = (JSONObject)json.get("totals");
        double sTotPay = (Double)totals.get("total_payments");
        double sTotPer = (Double)totals.get("total_percents");
        double sTotBas = (Double)totals.get("total_basics");
        //System.out.println("TOTALS// payments: "+sTotPay+" / percents: "+sTotPer+" / base: "+sTotBas);
        }catch (Exception e){}
    }
}
