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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CrcWebServiceController {
    String loggingPath = ".\\_admin_log.txt";
    private static HttpServletRequest request;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    @Autowired
    private CrcRequestRepository requestRepository;
    @Autowired
    private CrcUserRepository userRepository;
    
    @GetMapping("/")
    public String hello(){ return "/customLogin";}
    
    @GetMapping("/onceagain")
    public ModelAndView onceAgain(Authentication authentication){
        return new ModelAndView("user", Collections.singletonMap("login", authentication.getName()));
    }  
     
    @GetMapping("/showrequests")
    public ModelAndView showRequests(Authentication authentication, CrcRequestData data){
        try{
            String dateStart = data.getDateStart();
            String dateFinish = data.getDateFinish();
        Map response = new HashMap();
            response.put("result", logReader(dateStart, dateFinish));
            response.put("login", authentication.getName());
            response.put("previousStart", dateStart);
            response.put("previousFinish", dateFinish);
        return new ModelAndView("admin_show", response);
        }catch(Exception e){
            //System.out.println("ERROR: "+e);
            return new ModelAndView("admin", Collections.singletonMap("login", authentication.getName()));
        }
    }
    
    @GetMapping("/showusers")
    public ModelAndView showUsers(Authentication authentication){
        try{
        Map response = new HashMap(); 
            response.put("login", authentication.getName());
            List<CrcUser> users = userRepository.findAll();
            /*List<String> users = sqlReader("USERNAME", "USERS", "USERNAME");
            List<String> emails = sqlReader("EMAIL", "USERS", "USERNAME");
            List<String> states = sqlReader("ENABLED", "USERS", "USERNAME");
            List<String> roles = sqlReader("ROLE", "USERS_ROLES", "USERNAME");*/
            String result = userJsonBuilder(users);
            response.put("result", result);
        return new ModelAndView("admin_users", response);
        }catch(Exception e){
            return new ModelAndView("admin", Collections.singletonMap("login", authentication.getName()));
        }
    }
    
    @GetMapping("/administrate")
    public ModelAndView administrate(Authentication authentication, CrcRequestData data){
        String updaterole=data.getUpdaterole();
        String updateuser=data.getUpdateuser();
        String sqlQuery = "update USERS_ROLES set ROLE=? where USERNAME=?";
        jdbcTemplate.update(sqlQuery, updaterole, updateuser);
        return showUsers(authentication);
    }
    
    @GetMapping("/login")
    public ModelAndView login (Authentication authentication){
        boolean admin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        if(admin){
            return new ModelAndView("admin", Collections.singletonMap("login", authentication.getName()));
        }
        return new ModelAndView("user", Collections.singletonMap("login", authentication.getName()));
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
        String id = time+"_"+login;

            try{
                time=time.replace('/', '-');
                SimpleDateFormat fdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dateA = fdate.parse(time);
                //java.sql.Timestamp tstamp= new java.sql.Timestamp(dateA.getTime());
                //java.sql.Date keydate = new java.sql.Date(dateA.getTime());
                CrcRequest newrequest = new CrcRequest();    
                newrequest.setId(id);
                newrequest.setDatetime(time);
                newrequest.setUsername(login);
                newrequest.setIpaddress(ip);
                newrequest.setUri(uri);
                newrequest.setParams(params);
                newrequest.setResult(state);
                newrequest.setResultsize(size);
                newrequest.setKeydate(dateA);
                requestRepository.save(newrequest); 
                //jdbcTemplate.update(sqlQuery, id, tstamp, login, ip, uri, params, state, size, keydate);
            }catch (Exception e){System.out.println("Fail to log: "+time+"//"+login+"//"+state+": "+e);}
    }
    
    /*public void logger (String time, String login, String ip, String uri, String params, String state, int size){
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
    }*/
    
    /*public void logger (String time, String login, String ip, String uri, String params, String state, int size){
        String id = time+"_"+login;
        String sqlQuery = "insert into REQUESTSLOG values (?,?,?,?,?,?,?,?,?)";
            try{
                time=time.replace('/', '-');
                SimpleDateFormat fdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dateA = fdate.parse(time);
                java.sql.Timestamp tstamp= new java.sql.Timestamp(dateA.getTime());
                java.sql.Date keydate = new java.sql.Date(dateA.getTime());
                jdbcTemplate.update(sqlQuery, id, tstamp, login, ip, uri, params, state, size, keydate);
            }catch (Exception e){System.out.println("Fail to log: "+time+"//"+login+"//"+state+": "+e);}
    }*/
    
    /*private String logReader(){
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
    }*/
    
    private String logReader(String dateStart, String dateFinish){
        String log="";
        List<CrcRequest> result;
        try{
            if (dateStart.isEmpty() && dateFinish.isEmpty()){
                result = requestRepository.findAllByOrderByDatetime();
                /*for (int i=0; i<result.size(); i++){
                    System.out.println("RESULT: "+result.get(i).getId());
                }*/
                log = historyJsonBuilder(result);
            }else if (!dateStart.isEmpty() && dateFinish.isEmpty()){
                result = requestRepository.findAllWithKeydateAfter(sqlDate(dateStart));
                log = historyJsonBuilder(result);
            }else if (dateStart.isEmpty() && !dateFinish.isEmpty()){
                result = requestRepository.findAllWithKeydateBefore(sqlDate(dateFinish));
                log = historyJsonBuilder(result);
            }else{
                result = requestRepository.findAllWithRange(sqlDate(dateStart), sqlDate(dateFinish));
                log = historyJsonBuilder(result);
            }
        }catch (Exception e){
        System.out.println("ERROR: "+e);
        }
        return log;
    }
    
    public Date sqlDate(String baseDate) throws Exception{
        Date changedDate = new SimpleDateFormat("yyyy-MM-dd").parse(baseDate);
        return changedDate;
        /*int n = baseDate.indexOf("-");
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
        
        if (mm.equals("01")){
            mm="JAN";
        }else if (mm.equals("02")){
            mm="FEB"; 
        }else if (mm.equals("03")){
            mm="MAR";
        }else if (mm.equals("04")){
            mm="APR";
        }else if (mm.equals("05")){
            mm="MAY";
        }else if (mm.equals("06")){
            mm="JUN";
        }else if (mm.equals("07")){
            mm="JUL";
        }else if (mm.equals("08")){
            mm="AUG";
        }else if (mm.equals("09")){
            mm="SEP";
        }else if (mm.equals("10")){
            mm="OCT";
        }else if (mm.equals("11")){
            mm="NOV";
        }else if (mm.equals("12")){
            mm="DEC";
        }
        
        changedDate = dd+"-"+mm+"-"+yyyy;

        return changedDate;*/
    }
    /*
    private List<String> sqlReader(String column, String table, String order){
        String sqlQuery = "select "+column+" from "+table+" order by "+order;
        List<String> list = jdbcTemplate.queryForList(sqlQuery, String.class);
        return list;
    }
    
    private List<String> sqlReader(String column, String table, String order, String tstamp, boolean start){
        String sqlQuery = "select "+column+" from "+table+" where KEYDATE ";
        if (start){
            sqlQuery += ">= '"+tstamp+"'";
        }else{
            sqlQuery += "<= '"+tstamp+"'";
        }
        sqlQuery += " order by "+order;
        List<String> list = jdbcTemplate.queryForList(sqlQuery, String.class);
        return list;
    }
    
    private List<String> sqlReader(String column, String table, String order, String start, String finish){
        String sqlQuery = "select "+column+" from "+table+" where KEYDATE >= '" +start+"' and KEYDATE <= '"+finish+"' order by "+order;
        List<String> list = jdbcTemplate.queryForList(sqlQuery, String.class);
        return list;
    }
    */
    private String userJsonBuilder(List<CrcUser> users){
        String result="[";
        CrcUser user;
            for (int i=0; i<users.size(); i++){
                user=users.get(i);
                result += "{\"username\":\""+user.getUsername()+"\",\"email\":\""+user.getEmail()+"\",\"enabled\":\""+user.getEnabled()+"\",\"role\":\""+user.getRole().getRole()+"\"},";
            }
            result = result.substring(0,result.length()-1);
            result += "]";
        return result;
    }
        
    private String historyJsonBuilder(List<CrcRequest> list){
        CrcRequest request;
        String result="[";
            for (int i=0; i<list.size(); i++){
                request = list.get(i);
                        //System.out.println("GET KEYDATE: "+request.getKeydate());
                result += "{\"time\":\""+request.getDatetime()+"\",\"login\":\""+request.getUsername()+"\",\"ip\":\""+request.getIpaddress()+"\",\"uri\":\""+request.getUri()+"\",\"params\":\""+request.getParams()+"\",\"state\":\""+request.getResult()+"\",\"size\":"+request.getResultsize()+"},";
            }
            result = result.substring(0,result.length()-1);
            result += "]";
        return result;
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
