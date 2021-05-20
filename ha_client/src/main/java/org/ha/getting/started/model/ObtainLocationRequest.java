package org.ha.getting.started.model;
import java.io.Serializable;


class ObtainLocationReportRequest implements Serializable {

    String userId;
    int epoch;
    String haId;
    String haSignature;


    public ObtainLocationReportRequest(String userId, int epoch, String haId, String haSignature){

        this.userId = userId;
        this.epoch = epoch;
        this.haId = haId;
        this.haSignature = "unsgined";
    }



}