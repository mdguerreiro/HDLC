package org.acme.getting.started.model;

import java.io.Serializable;

public class ReadRegisterRequest implements Serializable {
    public int rid;
    public int epoch;
    public int x;
    public int y;
    public String username;
    public String senderServerName;
    public String signatureBase64;

    public ReadRegisterRequest(){

    }

    public ReadRegisterRequest(int rid, String senderServerName, String username, String signatureBase64){
       this.rid = rid;
       this.username = username;
       this.senderServerName = senderServerName;
       this.signatureBase64 = signatureBase64;
    }

    public ReadRegisterRequest(int rid, int x, int y, int epoch, String senderServerName, String signatureBase64){
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.epoch = epoch;
        this.senderServerName = senderServerName;
        this.signatureBase64 = signatureBase64;
    }
}
