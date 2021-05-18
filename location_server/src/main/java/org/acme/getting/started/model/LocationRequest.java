package org.acme.getting.started.model;

import java.io.Serializable;

public class LocationRequest implements Serializable {

    public String username;
    public int epoch;
    public String signatureBase64;

    public LocationRequest(){

    }

    public LocationRequest(String username, int epoch, String signatureBase64){
        this.username = username;
        this.epoch = epoch;
        this.signatureBase64 = signatureBase64;
    }

}
