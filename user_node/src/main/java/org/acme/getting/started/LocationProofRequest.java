package org.acme.getting.started;



public class LocationProofRequest {

    public String username;
    public int xLoc, yLoc;
    public byte[] signature;

    public LocationProofRequest(){

    }

    public LocationProofRequest(String username, int xLoc, int yLoc, byte[] signature){
        this.username = username;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.signature = signature;
    }

}
