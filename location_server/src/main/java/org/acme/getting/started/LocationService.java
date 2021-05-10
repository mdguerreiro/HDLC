package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Singleton
public class LocationService {
    private static final Logger LOG = Logger.getLogger(LocationService.class);
    private ConcurrentHashMap<String, ConcurrentHashMap> users;

    @Inject
    SignatureService signatureService;

    public LocationService() {
        this.users = null;
        try{
            deserializeData();
        } catch (Exception e) {
            this.users = new ConcurrentHashMap<>();
        }
    }

    public String submit_location_report(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        ConcurrentHashMap<Integer, LocationReport> location_reports = new ConcurrentHashMap<>();
        LOG.info(String.format("Received location report submission from %s at epoch %d - checking validity", lr.username, lr.epoch));
        int f = Integer.parseInt(System.getenv("BYZANTINE_USERS"));
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignature(lr.username, lr.epoch, lr.x, lr.y, lr.replies, lr.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("Signature Validation Failed. Aborting");
            return "Failed";
        }
        ArrayList<LocationProofReply> replies = lr.replies;
        int counter = 0;
        for(LocationProofReply reply : replies){
            if(reply.status.equals("APPROVED")){
                counter++;
            }
        }
        LOG.info("Number of approved " + counter);
        if(counter >= f + 1){
            LOG.info("There is byzantine consensus, request was approved.");
            location_reports.put(lr.epoch, lr);
            users.put(lr.username, location_reports);
            serializeData();
            return "Submitted";
        }
        else{
            LOG.info("There isn't byzantine consensus, request was denied.");
        }
        return "Failed";
    }

    public String get_location_report(String username, int epoch, String signatureBase64) {
        System.out.println("USERNAME " + username);
        System.out.println("EPOCH " + epoch);
        LocationReport lr;
        try{
            lr = (LocationReport) users.get(username).get(epoch);
        }catch (NullPointerException e){
            return "Not found";
        }
        System.out.println("DONE");
        System.out.println(users.toString());
        return String.format("User %s was at location x:%s y:%s", lr.username, lr.x, lr.y);
    }

    public String get_user_at(int x, int y, int epoch) {
        ArrayList<String> users_at_loc = new ArrayList<>();
        LocationReport lr;
        try{
            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry)it.next();
                lr = (LocationReport) users.get(pair.getKey()).get(epoch);
                if(lr.x == x && lr.y == y){
                    users_at_loc.add((String) pair.getKey());
                }
            }
        }catch (NullPointerException e){
            return "Not found";
        }
        System.out.println("DONE");
        return users_at_loc.toString();
    }

    public void serializeData(){
        try
        {
            FileOutputStream fos =
                    new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.users);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void deserializeData(){
        try
        {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.users = (ConcurrentHashMap) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();

        }
        catch(ClassNotFoundException c)
        {
            LOG.error("Class not found");
            c.printStackTrace();
            return;
        }
    }
}
