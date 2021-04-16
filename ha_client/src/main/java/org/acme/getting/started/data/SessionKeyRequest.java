package org.acme.getting.started.data;




public class SessionKeyRequest {

    public String userId;
    public int nonce;

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
