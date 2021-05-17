package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterReply implements Serializable {
    public int value;

    public WriteRegisterReply(){

    }

    public WriteRegisterReply(int value){
        this.value = value;
    }
}
