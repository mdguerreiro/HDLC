package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterRequest implements Serializable {
    public LocationReport locationReport;
    public String senderServerName;
    public int wts;

    public WriteRegisterRequest(){
    }

    public WriteRegisterRequest(LocationReport locationReport, String senderServerName, int wts){
        this.locationReport = locationReport;
        this.senderServerName = senderServerName;
        this.wts = wts;
    }
}
