package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterReply implements Serializable {
    public int value;
    public String signatureBase64;
    public String senderServerName;
    public String acknowledgment;
    public int ts;

    public WriteRegisterReply(){

    }

    public WriteRegisterReply(int value, String signatureBase64, String senderServerName, String acknowledgment){
        this.value = value;
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
        this.acknowledgment = acknowledgment;
        this.ts = ts;
    }
}
