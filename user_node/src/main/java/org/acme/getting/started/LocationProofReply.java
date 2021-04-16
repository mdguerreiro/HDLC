package org.acme.getting.started;
import java.io.Serializable;

public class LocationProofReply implements Serializable{

    public String status;
    public String signer;
    public String signatureBase64;

    public LocationProofReply(){
    }

    public LocationProofReply(String status, String signer, String signatureBase64){
        this.status = status;
        this.signer = signer;
        this.signatureBase64 = signatureBase64;
    }

}
