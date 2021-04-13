package org.acme.getting.started;



public class SignedSessionKeyRequest {

    public SessionKeyRequest sessionRequest;
    public byte[] signature;

    public SignedSessionKeyRequest(){

    }

    public SignedSessionKeyRequest(SessionKeyRequest sessionKeyRequest, byte[] signature){
        this.sessionKeyRequest = sessionRequest;
        this.signature = signature;
    }

}
