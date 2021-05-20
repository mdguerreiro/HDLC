package org.ha.getting.started.model;
import java.io.Serializable;


class ObtainUsersAtLocationRequest implements Serializable {

    int x;
    int y;
    int nonce;
    String haId;
    String haSignature;


    public ObtainUsersAtLocationRequest(int x, int y,int nonce,  String haId, String haSignature){

        this.x = x;
        this.y = y;
        this.nonce = nonce;
        this.haId = haId;
        this.haSignature = "unsgined";
    }



}