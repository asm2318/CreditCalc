/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

public class CrcRequestData {
    private String summ, rate, firstpay, type, term, date, login, updaterole, updateuser, dateStart, dateFinish;
    
    public String getSumm(){
        return summ;
    }
    public void setSumm(String summ){
        this.summ = summ;
    }
    public String getRate(){
        return rate;
    }
    public void setRate(String rate){
        this.rate = rate;
    }
    public String getFirstpay(){
        return firstpay;
    }
    public void setFirstpay(String firstpay){
        this.firstpay = firstpay;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getTerm(){
        return term;
    }
    public void setTerm(String term){
        this.term = term;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getLogin(){
        return login;
    }
    public void setLogin(String login){
        this.login = login;
    }
    public String getUpdaterole(){
        return updaterole;
    }
    public void setUpdaterole(String updaterole){
        this.updaterole = updaterole;
    }
    public String getUpdateuser(){
        return updateuser;
    }
    public void setUpdateuser(String updateuser){
        this.updateuser = updateuser;
    }
    public String getDateStart(){
        return dateStart;
    }
    public void setDateStart(String dateStart){
        this.dateStart = dateStart;
    }
    public String getDateFinish(){
        return dateFinish;
    }
    public void setDateFinish(String dateFinish){
        this.dateFinish = dateFinish;
    }
}
