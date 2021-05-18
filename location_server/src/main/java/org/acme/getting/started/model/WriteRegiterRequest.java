package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegiterRequest implements Serializable {
    public int value;
    public String signatureBase64;
    public String senderServerName;

    public WriteRegiterRequest(){

    }

    public WriteRegiterRequest(int value, String signatureBase64, String senderServerName){
        this.value = value;
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
    }

    public int getValue(){
        return value;
    }
}
