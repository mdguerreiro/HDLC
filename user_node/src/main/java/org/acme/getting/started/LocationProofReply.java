package org.acme.getting.started;

public class LocationProofReply {

    public String status;
    public String signer;
    public byte[] signature;

    public LocationProofReply(){
    }

    public LocationProofReply(String status, String signer, byte[] signature){
        this.status = status;
        this.signer = signer;
        this.signature = signature;
    }

}
