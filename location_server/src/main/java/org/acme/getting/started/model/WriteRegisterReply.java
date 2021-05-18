package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterReply implements Serializable {
    public int value;
    public String signatureBase64;
    public String senderServerName;

    public WriteRegisterReply(){

    }

    public WriteRegisterReply(int value, String signatureBase64, String senderServerName){
        this.value = value;
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
    }
}
