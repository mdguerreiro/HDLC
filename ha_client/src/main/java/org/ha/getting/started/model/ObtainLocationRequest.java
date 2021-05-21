package org.ha.getting.started.model;
import java.io.Serializable;


public class ObtainLocationRequest implements Serializable {

    String userId;
    int epoch;
    int nonce;
    String haId;
    String haSignature;


    public ObtainLocationRequest(String userId, int epoch,int nonce, String haId, String haSignature){

        this.userId = userId;
        this.epoch = epoch;
        this.nonce = nonce;
        this.haId = haId;
        this.haSignature = haSignature;
    }


    public String getUserId(){
        return userId;
    }
    public int getEpoch(){
        return epoch;
    }
    public int getNonce(){
        return nonce;
    }

    public String getHaId(){
        return haId;
    }

    public String getHaSignature(){
        return haSignature;
    }


    public void setSignature(String base64Signature){
        this.haSignature = base64Signature;
    }


}