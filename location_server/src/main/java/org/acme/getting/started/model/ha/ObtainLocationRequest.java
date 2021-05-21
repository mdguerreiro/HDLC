package org.acme.getting.started.model.ha;
import java.io.Serializable;


public class ObtainLocationRequest implements Serializable {

    String userId;
    int epoch;
    String haId;
    String haSignature;


    public ObtainLocationRequest(String userId, int epoch, String haId, String haSignature){

        this.userId = userId;
        this.epoch = epoch;
        this.haId = haId;
        this.haSignature = "unsgined";
    }


    public String getUserId(){
        return userId;
    }
    public int getEpoch(){
        return epoch;
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