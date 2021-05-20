package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterReply implements Serializable {

    public String acknowledgment;
    public String signatureBase64;
    public int ts;

    public WriteRegisterReply(){
    }

    public WriteRegisterReply(String acknowledgment, int ts){
        this.acknowledgment = acknowledgment;
        this.ts = ts;
    }

    public WriteRegisterReply(String acknowledgment, String signatureBase64, int ts){
        this.acknowledgment = acknowledgment;
        this.signatureBase64 = signatureBase64;
        this.ts = ts;
    }
}
