package org.acme.getting.started;



public class LocationProofRequest {

    public String username;
    public int xLoc, yLoc;

    public LocationProofRequest(){

    }

    public LocationProofRequest(String username, int xLoc, int yLoc){
        this.username = username;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

}
