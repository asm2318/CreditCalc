/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Алексей
 */
public class CrcModel {
    private static final int LIMIT = 4;
    static String importPath, exportPath, serialPath;
    String sep = "\\";
    final String newline = System.getProperty("line.separator");
    final int nl = newline.length();
    int result, lost;
    ArrayList<String> listID, listPaydate, listLoan, listPercent, listPayment, listTerm, listAnnu;
    ArrayList<ArrayList> lists;
    File importDir, exportDir, serializeDir;
    CrcController controller;
    public void startWork(CrcController controller){
        lost = 0;
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
        exportPath = ".\\_export\\";
        serialPath = ".\\_serialized\\";
        
        //for ide
        
        importDir = new File(importPath);
        exportDir = new File(exportPath);
        serializeDir = new File(serialPath);
        
        checkFolders(importDir, exportDir, serializeDir);
        importer(importDir);
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
        ArrayList<String> listCurr;
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
    
    
    private void readLists(boolean half, ArrayList<ArrayList> object){
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
           ExecutorService executor = Executors.newFixedThreadPool(LIMIT); 
            try{
            for (int i=j; i<listsize; i++){
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
                Runnable calculator = new CrcCalculator(sID, sLoan, sPercent, sPayment, annu, sTerm, sPaydate, exportPath);
                executor.execute(calculator);
            }
            if (half){
               serialize(lists);
               deserialize();
            }
            
           }catch (Exception e){
               System.out.println(e);
           }finally{
                executor.shutdown();
                try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {}
           }
        }
    }
    
    private void serialize(ArrayList<ArrayList> object){
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
        ArrayList<ArrayList> object = (ArrayList)ois.readObject();
        ois.close();
        fis.close();
        readLists(false, object);
        }catch (Exception e){}
    }
}
