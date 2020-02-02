/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CrcWebServiceController {
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "admin";
    String loggingPath = ".\\_admin_log.txt";
    private static HttpServletRequest request;
    
    @GetMapping("/")
    public String hello(){ return "login";}
    
    @GetMapping("/onceagain")
    public ModelAndView onceAgain(CrcHashData hashData){
        return new ModelAndView("user", Collections.singletonMap("login", hashData.getLogin()));
    }
    
    @GetMapping("/showrequests")
    public ModelAndView showRequests(CrcHashData hashData){
        try{
        Map response = new HashMap();
            response.put("result", logReader());
            response.put("login", hashData.getLogin());
        return new ModelAndView("admin_show", response);
        }catch(Exception e){
            return new ModelAndView("admin", Collections.singletonMap("login", hashData.getLogin()));
        }
    }
    
    @PostMapping("/login")
    public ModelAndView login (CrcLoginData loginData){
        if (LOGIN.equals(loginData.getLogin())){
            
            if(PASSWORD.equals(loginData.getPassword())){
            return new ModelAndView("admin", Collections.singletonMap("login", loginData.getLogin()));
            }else{
            return new ModelAndView("failure", Collections.singletonMap("login", loginData.getLogin()));
            }
        }
        return new ModelAndView("user", Collections.singletonMap("login", loginData.getLogin()));
    }
    
    @PostMapping("/count")
    public ModelAndView mvRequest (CrcRequestData requestData){
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String now = dateFormat.format(currentDate);
        String ip = getClientIp();
        String uri = getRequestURI();
        String params = "";
        int size = 0;
        try{
            String sID = requestData.getLogin();
            double sLoan = Double.valueOf(requestData.getSumm());
            double sPercent = Double.valueOf(requestData.getRate());
            double sPayment = 0;
            if(requestData.getFirstpay()!= null && !requestData.getFirstpay().isEmpty()) sPayment = Double.valueOf(requestData.getFirstpay());
            boolean annu = requestData.getType().equals("true");
            int sTerm = Integer.valueOf(requestData.getTerm());
            String sPaydate = changeDate(requestData.getDate()); 
            String nowId = now.replace(' ','_');
            nowId = nowId.replace('/','_');
            nowId = nowId.replace(':','_');
            sID=sID+"_"+nowId;
            //System.out.println(sID+"/"+sLoan+"/"+sPercent+"/"+sPayment+"/"+annu+"/"+sTerm+"/"+sPaydate);
            CrcCalculator calculator = new CrcCalculator(sID, sLoan, sPercent, sPayment, annu, sTerm, sPaydate);
            Map response = new HashMap();
            response.put("result", calculator.result());
            response.put("login", requestData.getLogin());
            params = sID+"/"+sLoan+"/"+sPercent+"/"+sPayment+"/"+annu+"/"+sTerm+"/"+sPaydate;
            byte[] resultbytes = calculator.result().getBytes("UTF-8");
            size = resultbytes.length;
            logger(now, requestData.getLogin(), ip, uri, params, "OK", size);
            return new ModelAndView("count", response);
        }catch (Exception e){
            //System.out.println("ERROR: "+e);
            Map response = new HashMap();
            response.put("login", requestData.getLogin());
            response.put("summ", requestData.getSumm());
            response.put("rate", requestData.getRate());
            response.put("firstpay", requestData.getFirstpay());
            response.put("type", requestData.getType());
            response.put("term", requestData.getTerm());
            response.put("date", requestData.getDate());
            params = "/"+requestData.getSumm()+"/"+requestData.getRate()+"/"+requestData.getFirstpay()+"/"+requestData.getType()+"/"+requestData.getTerm()+"/"+requestData.getDate();
            logger(now, requestData.getLogin(), ip, uri, params, "ERROR", 0);
            return new ModelAndView("error510", response);
        }
    }
    
    @ResponseBody
    @PostMapping("/message")
    public CrcOutputResource sendMessage(@RequestBody CrcInputResource inputResource){
        return new CrcOutputResource("Received: "+inputResource.getRequestMessage());
    }
       
    public String changeDate(String baseDate){
        String changedDate;
        int n = baseDate.indexOf("-");
        String dd = "";
        String mm = "";
        String yyyy = "";

        if (n!=-1){
            yyyy = baseDate.substring(0,n);
            changedDate = baseDate.substring(n+1);
            int m = changedDate.indexOf("-");
            if(m!=-1){
               mm = changedDate.substring(0,m);
               dd = changedDate.substring(m+1);
            }
        }
        changedDate = dd+"."+mm+"."+yyyy;
        return changedDate;
    }
    
    public void logger (String time, String login, String ip, String uri, String params, String state, int size){
        File fileLog = new File(loggingPath);
        String log = "";
            try{
                if(!fileLog.exists()){
                    fileLog.createNewFile();
                    logger(time, login, ip, uri, params, state, size);
                }else{
                    log += "{\"time\":\""+time+"\",\"login\":\""+login+"\",\"ip\":\""+ip+"\",\"uri\":\""+uri+"\",\"params\":\""+params+"\",\"state\":\""+state+"\",\"size\":"+size+"},";
                    BufferedWriter writer = new BufferedWriter(new FileWriter (fileLog, true));
                    writer.append(System.getProperty("line.separator"));
                    writer.append(log);
                    writer.flush();
                    writer.close();
                }
            }catch (Exception e){System.out.println("Fail to log: "+time+"//"+login+"//"+state+": "+e);}
    }
    
    private String logReader(){
        String log="[";
        File fileLog = new File(loggingPath);
            try{
                    BufferedInputStream stream = new BufferedInputStream (new FileInputStream(fileLog));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int r = 0;
                    while((r = stream.read())!=-1){
                        out.write((byte)r);
                    }
                    log += out.toString();
                    log = log.substring(0,log.length()-1);
                    log += "]";                             
            }catch (Exception e){ System.out.println("Fail to read log: "+e); }  
        return log;
    }
    
        @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    private static String getClientIp() {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("/count");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
    
    private static String getRequestURI(){
        String requestURI = "";
        if (request != null) {
            requestURI= request.getRequestURI();
        }
        return requestURI;
    }
    
}
