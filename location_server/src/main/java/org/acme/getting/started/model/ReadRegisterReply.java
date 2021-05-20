package org.acme.getting.started.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ReadRegisterReply implements Serializable {
    public String signatureBase64;
    public String senderServerName;
    public LocationReport lr;
    public int ts;
    public int rid;
    public HashMap<String, HashMap> map;

    public ReadRegisterReply(){

    }

    public ReadRegisterReply(String signatureBase64, String senderServerName, int ts, int rid, HashMap<String, HashMap> map){
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
        this.ts = ts;
        this.ts = rid;
        this.map = map;

    }
}
