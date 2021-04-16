package org.acme.getting.started;


import java.io.Serializable;

public class SessionKeyRequest implements Serializable {

    public String userId;
    public int nonce;

    public SessionKeyRequest(){

    }

    public SessionKeyRequest(String userId, int nonce){
        this.userId = userId;
        this.nonce = nonce;
    }

    public String getUserId(){
        return userId;
    }

    public int getNonce(){
        return nonce;
    }
}
