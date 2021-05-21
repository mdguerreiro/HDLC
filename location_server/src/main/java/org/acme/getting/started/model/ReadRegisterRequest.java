package org.acme.getting.started.model;

import java.io.Serializable;

public class ReadRegisterRequest implements Serializable {
    public int rid;
    public int epoch;
    public int x;
    public int y;
    public String username;
    public String senderServerName;

    public ReadRegisterRequest(){

    }

    public ReadRegisterRequest(int rid, String senderServerName, String username){
       this.rid = rid;
       this.username = username;
       this.senderServerName = senderServerName;
    }

    public ReadRegisterRequest(int rid, int x, int y, int epoch, String senderServerName){
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.epoch = epoch;
        this.senderServerName = senderServerName;
    }
}
