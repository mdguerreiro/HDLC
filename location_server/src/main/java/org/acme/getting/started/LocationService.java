package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.*;
import org.acme.getting.started.resource.ReadRegisterClient;
import org.acme.getting.started.resource.WriteRegisterClient;
import org.acme.getting.started.resource.WriteRegisterResource;
import org.acme.getting.started.storage.LocationReportsStorage;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.glassfish.json.JsonUtil;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Integer;
import java.lang.Boolean;

@Singleton
public class LocationService {
    private static final Logger LOG = Logger.getLogger(LocationService.class);
    protected HashMap<String, HashMap<Integer, LocationReport>> users;
    protected int data_ts;
    private final ConcurrentHashMap<String, ConcurrentHashMap> noncesOfUser;
    protected int write_timestamp;
    protected int rid;
    protected List<DataVersion> read_list;
    private int f;

    @Inject
    SignatureService signatureService;

    public LocationService() {
        this.users = new HashMap<>();
        this.noncesOfUser = new ConcurrentHashMap<>();
        this.write_timestamp = 0;
        this.data_ts = 0;
        this.f = Integer.parseInt(System.getenv("BYZANTINE_USERS"));
        this.read_list = new ArrayList<>();
    }

    public String validateLocationReport(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        //check wether the location report contains a nonce that has already been received
        if (!isValidLocationReportNonce(lr.username, lr.nonce)) {
            return String.format("Invalid nonce - %d", lr.nonce);
        }
        addUserNonce(lr.username, lr.nonce);

        HashMap<Integer, LocationReport> location_reports = new HashMap<>();
        LOG.info(String.format("Received location report submission from %s at epoch %d - checking validity, %d - nonce", lr.username, lr.epoch, lr.nonce));
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignature(lr.username, lr.epoch, lr.x, lr.y, lr.replies, lr.signatureBase64);

        if (!isSignatureCorrect) {
            LOG.info("Signature Validation Failed. Aborting");
            return "Signature Failed";
        }
        ArrayList<LocationProofReply> replies = lr.replies;
        int counter = 0;
        for (LocationProofReply reply : replies) {
            if (reply.status.equals("APPROVED")) {
                counter++;
            }
        }
        LOG.info("Number of approved " + counter);
        if (counter >= (f + 1)) {
            LOG.info("There is byzantine consensus, request was approved.");
            location_reports.put(lr.epoch, lr);
            users.put(lr.username, location_reports);
            return "Submitted";
        } else {
            LOG.info("There isn't byzantine consensus, request was denied.");
        }
        return "Failed";
    }

    public String submit_location_report(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, URISyntaxException, UnrecoverableKeyException {
       // String locationReportValidationResult = validateLocationReport(lr);

        //if (!locationReportValidationResult.equals("Submitted")) {
       //     return locationReportValidationResult; // Do not send location report to replicas if val. result is failed
        //}

        String isWriter = System.getenv("IS_WRITER");
        if (isWriter.equals("true")) {
            int acknowledgments = 0;
            int replicasNumber = AppLifecycleBean.location_servers.entrySet().size();
            this.write_timestamp++;
            Iterator serversIterator = AppLifecycleBean.location_servers.entrySet().iterator();
            while (serversIterator.hasNext()) {
                String myServerName = System.getenv("SERVER_NAME");
                Map.Entry server = (Map.Entry) serversIterator.next();
                String serverName = (String) server.getKey();
                String serverUrl = (String) server.getValue();

                /* TODO FIX SIGNATURE */
                String signatureBase64 = "TEST";
//              String signatureBase64 = signatureService.generateSha256WithRSASignatureForWriteRegister(serverName, lr);


                WriteRegisterClient writeRegisterClient = RestClientBuilder.newBuilder()
                        .baseUri(new URI(serverUrl))
                        .build(WriteRegisterClient.class);
                WriteRegisterRequest writerRegisterRequest = new WriteRegisterRequest(lr, signatureBase64, myServerName,
                        this.write_timestamp);
                WriteRegisterReply writeRegisterReply = writeRegisterClient.submitWriteRegisterRequest(writerRegisterRequest);

                if (writeRegisterReply.acknowledgment.equals("true")) {
                    acknowledgments++;
                }

                if (acknowledgments > (replicasNumber + f) / 2) {
                    LOG.info("LOCATION SERVER: " + myServerName + " got acknowledgments from quorum replicas. Sending reply");
                    return "Submitted";
                }
            }

        }

        return "Failed";
    }

    public void readSync(String username, int epoch) throws URISyntaxException {
        LocationReport locationReport = null;
        int replicasNumber = AppLifecycleBean.location_servers.entrySet().size();
        read_list.clear();
        Iterator serversIterator = AppLifecycleBean.location_servers.entrySet().iterator();
        while (serversIterator.hasNext()) {
            Map.Entry server = (Map.Entry) serversIterator.next();
            String serverUrl = (String) server.getValue();

            ReadRegisterClient readRegisterClient = RestClientBuilder.newBuilder()
                    .baseUri(new URI(serverUrl))
                    .build(ReadRegisterClient.class);
            ReadRegisterRequest readRegisterRequest = new ReadRegisterRequest(this.rid);
            ReadRegisterReply readRegisterReply = readRegisterClient.submitReadRegisterRequest(readRegisterRequest);
            // TODO Verify signature
            locationReport = readRegisterReply.lr;

            DataVersion dv = new DataVersion(readRegisterReply.ts, locationReport);
            read_list.add(dv);
        }
        if(read_list.size() > (replicasNumber + f) / 2){
            int max = 0;
            for(DataVersion elem: read_list){
                if(elem.getTS() > max){
                    max = elem.getTS();
                    locationReport = elem.getData();
                }
            }
            read_list.clear();
        }

        HashMap<Integer, LocationReport> locationReportAtEpochHashMap = new HashMap<>();
        locationReportAtEpochHashMap.put(epoch, locationReport);
        this.users.put(username, locationReportAtEpochHashMap);
    }
    public String get_location_report(String username, int epoch, String signatureBase64) throws URISyntaxException {
        readSync(username, epoch);
        System.out.println("LOCATION REPORT!!");
        System.out.println(users.toString());
        System.out.println("USERNAME " + username);
        System.out.println("EPOCH " + epoch);
        LocationReport lr;
        try{
            lr = users.get(username).get(epoch);
        }catch (NullPointerException e){
            return "Not found";
        }
        System.out.println("DONE");
        return String.format("User %s was at location x:%s y:%s", lr.username, lr.x, lr.y);
    }

    public String get_user_at(int x, int y, int epoch) throws URISyntaxException {
        readSync(epoch, username);
        ArrayList<String> users_at_loc = new ArrayList<>();
        LocationReport lr;
        try {
            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                lr = users.get(pair.getKey()).get(epoch);
                if (lr.x == x && lr.y == y) {
                    users_at_loc.add((String) pair.getKey());
                }
            }
        } catch (NullPointerException e) {
            return "Not found";
        }

        return users_at_loc.toString();
    }


    public boolean isValidLocationReportNonce(String userId, int nonce) {

        ConcurrentHashMap<Integer, Boolean> userNoncesSet = noncesOfUser.get(userId);
        if (userNoncesSet == null) {
            return true;
        }
        if (userNoncesSet.containsKey(new Integer(nonce))) {
            return false;
        }
        LOG.info(userNoncesSet.size());
        return true;
    }


    public void addUserNonce(String userId, int nonce) {
        ConcurrentHashMap<Integer, Boolean> userNoncesSet = noncesOfUser.get(userId);
        if (userNoncesSet == null) {
            noncesOfUser.put(userId, new ConcurrentHashMap<Integer, Boolean>());
            noncesOfUser.get(userId).put(new Integer(nonce), new Boolean(true));
        }
        noncesOfUser.get(userId).put(new Integer(nonce), new Boolean(true));
    }
}
