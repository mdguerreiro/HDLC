package org.acme.getting.started;



public class SessionKeyRequest {

    public String userId;
    public int nonce;

    public SessionKeyRequest(String userId, int nonce){
        this.userId = userId;
        this.nonce = nonce;
    }

}
