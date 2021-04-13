package org.acme.getting.started;

import org.acme.getting.started.SessionKeyRequest;


public class SignedSessionKeyRequest {

    public SessionKeyRequest sessionKeyRequest;
    public byte[] signature;

    public SignedSessionKeyRequest(){

    }

    public SignedSessionKeyRequest(SessionKeyRequest sessionKeyRequest, byte[] signature){
        this.sessionKeyRequest = sessionKeyRequest;
        this.signature = signature;
    }

    public SessionKeyRequest getSessionKeyRequest(){
        return sessionKeyRequest;
    }

    public byte[] getSignature(){
        return signature;
    }

}
