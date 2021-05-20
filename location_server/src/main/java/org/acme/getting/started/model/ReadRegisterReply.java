package org.acme.getting.started.model;

import java.io.Serializable;

public class ReadRegisterReply implements Serializable {
    public String signatureBase64;
    public String senderServerName;
    public LocationReport lr;
    public int ts;
    public int rid;

    public ReadRegisterReply(){

    }

    public ReadRegisterReply(String signatureBase64, String senderServerName, LocationReport lr, int ts, int rid){
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
        this.ts = ts;
        this.ts = rid;
        this.lr = lr;
    }
}
