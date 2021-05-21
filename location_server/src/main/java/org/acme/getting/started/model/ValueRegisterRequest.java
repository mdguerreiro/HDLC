package org.acme.getting.started.model;

import java.io.Serializable;

public class ValueRegisterRequest implements Serializable {
    public int listening;
    public int ts;
    public LocationReport locationReport;

    public ValueRegisterRequest(){
    }

    public ValueRegisterRequest(LocationReport locationReport, int ts, int listening){
        this.locationReport = locationReport;
        this.ts = ts;
        this.listening = listening;
    }
}
