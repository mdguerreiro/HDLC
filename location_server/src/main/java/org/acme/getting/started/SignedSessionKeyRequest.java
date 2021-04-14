package org.acme.getting.started;

import org.acme.getting.started.SessionKeyRequest;
import org.apache.commons.codec.binary.Base64;

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


    public String toString(){
        String signatureString = "signature base64 str - {" + new String( Base64.encodeBase64(signature) )+ "}";

        String nonceString = "{number used once - " + Integer.toString(sessionKeyRequest.getNonce()) + "}";
        return sessionKeyRequest.getUserId() +"\n"+nonceString +  "\n" + signatureString;
    }
}
