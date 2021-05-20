package org.acme.getting.started.model;

import java.io.Serializable;

public class ReadRegisterRequest implements Serializable {
    public int rid;
    public int epoch;
    public String username;
    public String senderServerName;
    public String signatureBase64;

    public ReadRegisterRequest(){

    }

    public ReadRegisterRequest(int rid, String senderServerName, String signatureBase64){
       this.rid = rid;
       this.senderServerName = senderServerName;
       this.signatureBase64 = signatureBase64;
    }
}
