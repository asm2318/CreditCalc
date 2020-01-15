/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Алексей
 */
public class CrcCalculator implements Runnable {
   String id, paydate, exportPath, resPath;
   double loan, percent, payment;
   boolean annu;
   int term;
   CrcChannelHandler handler;
   
   CrcCalculator(String id, double loan, double percent, double payment, boolean annu, int term, String paydate, String exportPath, CrcChannelHandler handler){
            this.id = id;
            this.loan = loan;
            this.percent = percent;
            this.payment = payment;
            this.annu = annu;
            this.term = term;
            this.paydate = paydate;
            this.exportPath = exportPath;
            this.handler = handler;
        }
   
   
    @Override
    public void run(){
        calculator();
    }
     
    public String result(){
        calculator();
        return resPath;
    }
    
        private void calculator(){
        //System.out.println(id+"//"+loan+"//"+percent+"//"+payment+"//"+annu+"//"+term+"//"+paydate);
        double paysumm;
        double nper = percent/100/12;
        double rest = loan;
        ArrayList<Double> listSumms = new ArrayList<Double>();
        ArrayList<Double> listBasic = new ArrayList<Double>();
        if(annu){
           double p = nper+(nper/(Math.pow((1+nper),(double)term)-1));
           paysumm = loan*p;
           for (int i=0; i<term; i++){
            listSumms.add(paysumm);
            double basic = paysumm-rest*nper;
            listBasic.add(basic);
            rest = rest - basic;
           }
        }else{
           double basic = loan/term;
            for (int i=0; i<term; i++){
            paysumm = different(loan, nper, basic, i);
            listSumms.add(paysumm);
            listBasic.add(basic);
            }
        }
        
        ArrayList<Double> listCounted = payCounter(listSumms, payment, true);
        //ArrayList<Double> listBcounted = new ArrayList<Double>();
        /*if (payment!=0){
            double total = 0;
            for (int i=0; i<listBasic.size(); i++){
               double div = listBasic.get(i)/listSumms.get(i);
               System.out.println("basic: "+listBasic.get(i));
               double val = listCounted.get(i)*div;
               System.out.println("summs: "+listSumms.get(i));
               System.out.println("val: "+val);
               listBcounted.add(val);
               total = total+val;
            }
            listBcounted.add(new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        }else{
        listBcounted = payCounter(listBasic, 0, true);
        }*/
        double val = payment;
        if (payment!=0){
            double div = listCounted.get(0)/listSumms.get(0);
            val = payment*div;
            //System.out.println("counted: "+listCounted.get(0));
            //System.out.println("summs: "+listSumms.get(0));
            //System.out.println("val: "+val);
        }
        ArrayList<Double> listBcounted = payCounter(listBasic, val, true);
        ArrayList<Double> listPcounted = payCounter(listCounted, listBcounted);
        ArrayList<Double> listRcounted = payCounter(loan, listBcounted);
        
        ArrayList<Date> shedule = dateCounter(paydate, term);
        /*for (int x=0; x<listCounted.size()-1; x++){
            System.out.println(shedule.get(x)+"//"+listCounted.get(x)+"//"+listPcounted.get(x)+"//"+listBcounted.get(x)+"//"+listRcounted.get(x));
            if (x==listCounted.size()-2){
                System.out.println("Total://"+listCounted.get(x+1)+"//"+listPcounted.get(x+1)+"//"+listBcounted.get(x+1));
            }
        }*/
        
        createTable(id, loan, percent, payment, annu, term, paydate, shedule, listCounted, listPcounted, 
                listBcounted, listRcounted);

    }
    
    private void createTable(String id, double loan, double percent, double payment, boolean annu, 
            int term, String paydate, ArrayList<Date> dates, ArrayList<Double> pays, ArrayList<Double> lper,
            ArrayList<Double> lbase, ArrayList<Double> lrest){
        
        try{
        Format df = new SimpleDateFormat("dd.MM.yyyy");
        String contents = "{\"id\":"+id+",\"loan\":"+loan+",\"rate\":"+percent+",\"firstpay\":"+payment+",\"annuity\":"+annu+",\"term\":"+term+",\"firstpaydate\":"+paydate+",\"shedule\":[";
        for (int i=0; i<dates.size(); i++){
                contents = contents+"{\"date\":"+df.format(dates.get(i))+",\"summ_pay\":"+pays.get(i)+",\"percent_pay\":"+lper.get(i)+",\"basic_pay\":"+lbase.get(i)+",\"rest\":"+lrest.get(i)+"}";
                if (i<dates.size()-1) contents = contents+",";
        }
        contents=contents+"],\"totals\":{\"total_payments\":"+pays.get(pays.size()-1)+",\"total_percents\":"+lper.get(lper.size()-1)+",\"total_basics\":"+lbase.get(lbase.size()-1)+"}}";
        resPath = exportPath+id+".json";
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(resPath));
        stream.write(contents.getBytes());
        stream.close();
        //handler.response(fpath);
        /*FileChannel channel = new RandomAccessFile(exportPath+id+".json", "rw").getChannel();
        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, contents.length());

        buffer.put(contents.getBytes());
        channel.close();   */     
        }catch (Exception e){
            System.out.println(e);
        }
        
        
    }
    
    private ArrayList<Double> payCounter(ArrayList<Double> list, double first, boolean tot){
        double addition=0;
        double last = 0;
        double total = 0;
        
        if (first!=0){ // задана сумма первого платежа
        double prep = list.get(0);
        //System.out.println("first: "+first);
        //System.out.println("prep: "+prep);
            double dffrnc = prep-first;
            addition = dffrnc/(list.size()-1);
                for (int i=1; i<list.size(); i++){
                    //System.out.println("before: "+list.get(i));
                    list.set(i, list.get(i)+addition);
                    //System.out.println("after: "+(list.get(i)+addition));
                }
       list.set(0,first);
       addition = 0;
        }
        
        for (int i=0; i<list.size()-1; i++){
            double basic = list.get(i);
            double rnd = new BigDecimal(basic).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            addition = addition+basic-rnd;
            total=total+rnd;
            list.set(i,rnd);
        }
        
        last = list.get(list.size()-1)+addition;
        last = new BigDecimal(last).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        list.set(list.size()-1,last);
        if (tot){
        total = new BigDecimal(total+last).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        list.add(total);
        }
        return list;
    }
    
    private ArrayList<Double> payCounter(ArrayList<Double> listTotal, ArrayList<Double> listBase){
        ArrayList<Double> list = new ArrayList<Double>();
            for (int i=0; i<listTotal.size(); i++){
                double val = new BigDecimal(listTotal.get(i) - listBase.get(i)).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
                list.add(val);
            }
        return list;
    }
    
    private ArrayList<Double> payCounter(Double loan, ArrayList<Double> listBase){
        ArrayList<Double> list = new ArrayList<Double>();
        double rest = loan;
            for (int i=0; i<listBase.size(); i++){
                rest = new BigDecimal(rest - listBase.get(i)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                list.add(rest);
            }
        return list;
    }
    
    /*private double annuity(double loan, double percent){
        double payment = loan*percent;
        return payment;
    }*/
    private double different(double loan, double percent, double basic, int month){
        double payment = basic + (loan-(basic*month))*percent;
        return payment;
    }
    
       
    private ArrayList<Date> dateCounter (String first, int term){
        ArrayList<Date> counter = new ArrayList<Date>();
        String sdate;
        int n = first.indexOf(".");
        int dd = 0;
        int mm = 0;
        int yyyy = 0;
        
        if (n!=-1){
            dd = Integer.valueOf(first.substring(0,n));
            sdate = first.substring(n+1);
            int m = sdate.indexOf(".");
            if(m!=-1){
               mm = Integer.valueOf(sdate.substring(0,n));
               yyyy = Integer.valueOf(sdate.substring(n+1));
            }
        }
        sdate = first;
        
        for (int i=0; i<term; i++){
          try{
              if (mm>12){
                  mm=1;
                  yyyy++;
              }
              Date payday=checkDate(dd,mm,yyyy);
              counter.add(payday);
              mm++;
          }catch (Exception e){}
          
        }
        
        return counter;
    }
    
    private Date checkDate(int dd, int mm, int yyyy){
        Date payday;
            try{    
            payday=new SimpleDateFormat("dd.MM.yyyy").parse(dd+"."+mm+"."+yyyy);
            return checkDay(payday);
            }catch (Exception e){
               try{
               int mnt = mm+1;
               if (mnt>12){ 
                   mnt=1;
                   yyyy++;
               }
               payday=new SimpleDateFormat("dd.MM.yyyy").parse("01."+mnt+"."+yyyy); 
               return checkDay(payday);
               }catch (ParseException pe){
               System.out.println(pe);
               }
            }
        return null;
    }
    
    private Date checkDay(Date pdate) throws ParseException{
        String s = String.valueOf(pdate);
        if (s.startsWith("Sat")){
            Calendar c = Calendar.getInstance();
            c.setTime(pdate);
            c.add(Calendar.DATE, 2);
            pdate = c.getTime();
        }else if (s.startsWith("Sun")){
            Calendar c = Calendar.getInstance();
            c.setTime(pdate);
            c.add(Calendar.DATE, 1);
            pdate = c.getTime();
        }
        return pdate;
    }
    
    
}
