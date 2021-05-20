package org.ha.getting.started.model;


class ObtainLocationReportRequest implements
{

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