package org.acme.getting.started.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ReadRegisterReply implements Serializable {
    public String senderServerName;
    public LocationReport lr;
    public ArrayList<String> usersAtLocation;
    public int ts;
    public int rid;

    public ReadRegisterReply(){

    }

    public ReadRegisterReply(String senderServerName, LocationReport lr, int ts, int rid){
        this.senderServerName = senderServerName;
        this.ts = ts;
        this.ts = rid;
        this.lr = lr;
    }
}
