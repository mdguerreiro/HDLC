package org.ha.getting.started.model;
import java.io.Serializable;


public class ObtainUserAtLocationRequest implements Serializable {

    int x;
    int y;
    int nonce;
    String haId;
    String haSignature;


    public ObtainUserAtLocationRequest(int x, int y,int nonce,  String haId, String haSignature){

        this.x = x;
        this.y = y;
        this.nonce = nonce;
        this.haId = haId;
        this.haSignature = "unsgined";
    }


    public int getX(){
        return x;
    }

    public int getY(){
        return y;
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

    public void setSignature(String b64Signature){
        this.haSignature = b64Signature;
    }
}