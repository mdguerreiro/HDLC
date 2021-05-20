package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterReply implements Serializable {

    public String acknowledgment;
    public int ts;

    public WriteRegisterReply(){
    }

    public WriteRegisterReply(String acknowledgment, int ts){
        this.acknowledgment = acknowledgment;
        this.ts = ts;
    }
}
