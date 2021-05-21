package org.acme.getting.started.model;

import java.util.ArrayList;

public class DataVersionForUsersAtPosition {

    private int ts;
    public ArrayList<String> usersAtLocation;

    public DataVersionForUsersAtPosition(int ts, ArrayList<String> usersAtLocation){
        this.ts = ts;
        this.usersAtLocation = usersAtLocation;
    }

    public int getTS(){
        return ts;
    }

    public ArrayList<String> getData(){
        return usersAtLocation;
    }
}
