package org.acme.getting.started.model;

import java.io.Serializable;

public class WriteRegisterRequest implements Serializable {
    public LocationReport locationReport;
    public String signatureBase64;
    public String senderServerName;
    public int wts;

    public WriteRegisterRequest(){
    }

    public WriteRegisterRequest(LocationReport locationReport, String signatureBase64, String senderServerName, int wts){
        this.locationReport = locationReport;
        this.signatureBase64 = signatureBase64;
        this.senderServerName = senderServerName;
        this.wts = wts;
    }
}
