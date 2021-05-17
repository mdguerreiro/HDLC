package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegiterRequest implements Serializable {
    public int value;

    public WriteRegiterRequest(){

    }

    public WriteRegiterRequest(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
