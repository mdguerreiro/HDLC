package org.acme.getting.started.data;

import java.io.Serializable;

public class ObtainLocationReportRequest implements Serializable{

    int nonce;
    String userID;
    int epoch;
    byte[] hash;

    public ObtainLocationReportRequest(){}

    public ObtainLocationReportRequest(int nonce, String userID, int epoch, byte[] hash){
        this.nonce = nonce;
        this.userID = userID;
        this.epoch = epoch;
        this.hash  = hash;
    }

    public int getNonce(){
        return nonce;
    }

    public String getUserID(){
        return userID;
    }

    public int getEpoch(){
        return epoch;
    }

    public byte[] getHash() {return hash;}

    public void setHash(byte[] hash) { this.hash = hash;}

}


