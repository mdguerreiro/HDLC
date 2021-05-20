package org.acme.getting.started.model;

import java.io.Serializable;

public class ReadRegisterRequest implements Serializable {
    public int rid;
    public int epoch;
    public String username;

    public ReadRegisterRequest(){

    }

    public ReadRegisterRequest(int rid){
       this.rid = rid;
    }
}
