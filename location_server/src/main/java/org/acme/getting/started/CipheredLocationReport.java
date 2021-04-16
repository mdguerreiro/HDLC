package org.acme.getting.started;

import java.io.Serializable;

public class CipheredLocationReport implements Serializable {

    public String username;
    public byte[] cipheredLocationReportBytes;

    public CipheredLocationReport(){

    }

    public CipheredLocationReport( String username, byte[] cipheredLocationReportBytes){
        this.username = username;
        this.cipheredLocationReportBytes = cipheredLocationReportBytes;
    }

    public byte[] getCipheredLocationReportBytes(){
        return cipheredLocationReportBytes;
    }

    public String getUsername(){
        return username;
    }

}
