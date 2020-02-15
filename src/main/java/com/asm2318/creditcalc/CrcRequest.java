/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="REQUESTSLOG")
public class CrcRequest implements Serializable {
  
    @Id
    String id;
    @Temporal(TemporalType.DATE)
    Date keydate;
    
    String datetime, username, ipaddress, uri, params, result;
    int resultsize;
    
    public CrcRequest(){}

    public CrcRequest(String id, String datetime, String username, String ipaddress, String uri, String params, String result, int resultsize, Date keydate) {
        this.id = id;
        this.datetime = datetime;
        this.username = username;
        this.ipaddress = ipaddress;
        this.uri = uri;
        this.params = params;
        this.result = result;
        this.resultsize = resultsize;
        this.keydate = keydate;
    }
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
  
    public String getDatetime() {
        return datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getIpaddress() {
        return ipaddress;
    }
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }
    
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    
    public int getResultsize() {
        return resultsize;
    }
    public void setResultsize(int resultsize) {
        this.resultsize = resultsize;
    }
    
    public Date getKeydate() {
        return keydate;
    }
    public void setKeydate(Date keydate) {
        this.keydate = keydate;
    }
}
