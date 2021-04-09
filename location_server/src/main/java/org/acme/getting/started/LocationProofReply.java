package org.acme.getting.started;

public class LocationProofReply {

    public String status;
    public String signer;

    public LocationProofReply(){
    }

    public LocationProofReply(String status, String signer){
        this.status = status;
        this.signer = signer;
    }

}
