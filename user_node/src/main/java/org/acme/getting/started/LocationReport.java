package org.acme.getting.started;

import java.util.ArrayList;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;


public class LocationReport implements Serializable{

    private static final long serialVersionUID = 1;

    public String username, signatureBase64;
    public int x, y, epoch;
    public ArrayList<LocationProofReply> replies;

    public LocationReport(){

    }

    public LocationReport(String username, int epoch, int x, int y, ArrayList<LocationProofReply> replies, String signatureBase64){
        this.username = username;
        this.x = x;
        this.y = y;
        this.epoch = epoch;
        this.replies = replies;
        this.signatureBase64 = signatureBase64;
    }


    public static byte[] toBytes( LocationReport lr ) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        out = new ObjectOutputStream(bos);
        out.writeObject(lr);
        out.flush();
        byte[] locationReportBytes = bos.toByteArray();

        bos.close();

        return locationReportBytes;

    }

    public static LocationReport fromBytes( byte[] locationReportBytes) throws IOException, ClassNotFoundException{

        ByteArrayInputStream bis = new ByteArrayInputStream(locationReportBytes);

        ObjectInput in = null;
        in = new ObjectInputStream(bis);

        Object obj = in.readObject();

        in.close();

        return (LocationReport) obj;

    }

}
