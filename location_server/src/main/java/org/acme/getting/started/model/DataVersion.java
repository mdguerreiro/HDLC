package org.acme.getting.started.model;

public class DataVersion {

    private int ts;
    private LocationReport locationReport;

    public DataVersion(int ts, LocationReport lr){
        this.ts = ts;
        this.locationReport = lr;
    }

    public int getTS(){
        return ts;
    }

    public LocationReport getData(){
        return locationReport;
    }
}
