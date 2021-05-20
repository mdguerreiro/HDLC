package org.acme.getting.started.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DataVersion {

    private int ts;
    private HashMap map;

    public DataVersion(int ts, HashMap lr){
        this.ts = ts;
        this.map = lr;
    }

    public int getTS(){
        return ts;
    }

    public HashMap getData(){
        return map;
    }
}
