package org.acme.getting.started.model.ha;
import java.io.Serializable;


public class HaResponse implements Serializable {

    String text;
    int nonce;
    String serverSign;


    public HaResponse(String text,int nonce,String serverSign){

        this.text = text;
        this.nonce = nonce;
        this.serverSign = serverSign;
    }


    public String getText() {return text;}
    public String getServerSign() {return serverSign;}
    public int getNonce() {return nonce;}

    public void setSignature(String base64Signature){
        this.serverSign = serverSign;
    }


}