package org.acme.getting.started;


import java.io.Serializable;

public class LocationProofRequest implements Serializable {

    public String username;
    public int xLoc, yLoc;
    public String signatureBase64;

    public LocationProofRequest(){

    }

    public LocationProofRequest(String username, int xLoc, int yLoc, String signatureBase64){
        this.username = username;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.signatureBase64 = signatureBase64;
    }

}
