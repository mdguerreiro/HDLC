package org.acme.getting.started;

import java.util.ArrayList;

public class LocationReport {


    public String username;
    public int x, y, epoch;
    public ArrayList<LocationProofReply> replies;
    public String signatureBase64;

    public LocationReport(){

    }

    public LocationReport(String username, int x, int y, ArrayList<LocationProofReply> replies, String signatureBase64){
        this.username = username;
        this.x = x;
        this.y = y;
        this.replies = replies;
        this.signatureBase64 = signatureBase64;
    }

}
