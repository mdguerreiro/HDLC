package org.acme.getting.started.data;
import java.io.Serializable;


public class CipheredObtainLocationReportRequest implements Serializable{

    byte[] cipheredByteArray;

    public CipheredObtainLocationReportRequest(){}

    public CipheredObtainLocationReportRequest(byte[] cipheredByteArray){
        this.cipheredByteArray = cipheredByteArray;
    }

    public byte[] CipheredObtainLocationReportRequest(){
        return cipheredByteArray;
    }
}