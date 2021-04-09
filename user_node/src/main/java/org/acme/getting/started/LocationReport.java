package org.acme.getting.started;

import java.util.ArrayList;

public class LocationReport {


    public String username;
    public int x, y, epoch;
    public ArrayList<LocationProofReply> replies;

    public LocationReport(){

    }

    public LocationReport(String username, int epoch, int x, int y, ArrayList<LocationProofReply> replies){
        this.username = username;
        this.x = x;
        this.y = y;
        this.epoch = epoch;
        this.replies = replies;
    }

}
