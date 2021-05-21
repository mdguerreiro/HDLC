package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.LocationProofReply;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.persistance.User;
import org.acme.getting.started.model.*;
import org.acme.getting.started.resource.ReadRegisterClient;
import org.acme.getting.started.resource.WriteRegisterClient;
import org.acme.getting.started.storage.LocationReportsStorage;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Integer;
import java.lang.Boolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class LocationService {
    private static final Logger LOG = Logger.getLogger(LocationService.class);
    protected int data_ts;
    private final ConcurrentHashMap<String, ConcurrentHashMap> noncesOfUser;
    protected int write_timestamp;
    protected int rid;
    protected List<DataVersion> read_list;
    protected List<DataVersionForUsersAtPosition> read_list_for_users_at_pos;
    private int f;

    @Inject
    SignatureService signatureService;

    private HashMap<String, HashMap<Integer, LocationReport>> mapUsersFromMongoToJavaObjects(List<User> users) {
        HashMap<String, HashMap<Integer, LocationReport>> usersJavaObj = new HashMap<>();
        return usersJavaObj;
    }

    public LocationService() {
        LocationReportsStorage.users = null;

//        try{
//            Stream<User> users = User.streamAll();
//            List<User> allUsers = users.collect(Collectors.toList());
//            LOG.info("TEST");
//            if(allUsers.size() != 0) {
//                LocationReportsStorage.users = mapUsersFromMongoToJavaObjects(allUsers);
//            } else {
//                LocationReportsStorage.users = new HashMap<>();
//            }
//        } catch (Exception e) {
//            LocationReportsStorage.users = new HashMap<>();
//        }
        LocationReportsStorage.users = new HashMap<>();

        this.noncesOfUser = new ConcurrentHashMap<>();
        this.write_timestamp = 0;
        this.data_ts = 0;
        this.f = Integer.parseInt(System.getenv("BYZANTINE_USERS"));
        this.read_list = new ArrayList<>();
        this.read_list_for_users_at_pos = new ArrayList<>();
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
            LocationReportsStorage.users.put(lr.username, location_reports);
            insertUserToMongoDB(lr.username, location_reports);
            return "Submitted";
        } else {
            LOG.info("There isn't byzantine consensus, request was denied.");
        }
        return "Failed";
    }

    private void insertUserToMongoDB(String username, HashMap<Integer, LocationReport> locationReports) {
        User user = new User();
        user.setUsername(username);
        user.persist();
    }

    public String submit_location_report(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, URISyntaxException, UnrecoverableKeyException {
        /** If Writer does not submit location report locally,
         *  Then it should broadcast it to replicas
         *  Otherwise we get inconsistency **/
        String locationReportValidationResult = validateLocationReport(lr);
        if (!locationReportValidationResult.equals("Submitted")) {
            LOG.info("Location Report Validation Failed: Broadcast to replicas is interrupted");
            return locationReportValidationResult; // Do not send location report to replicas if val. result is failed
        }

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

                String signatureBase64 = signatureService.generateSha256WithRSASignatureForWriteRequest(myServerName, lr);

                WriteRegisterClient writeRegisterClient = RestClientBuilder.newBuilder()
                        .baseUri(new URI(serverUrl))
                        .build(WriteRegisterClient.class);
                WriteRegisterRequest writerRegisterRequest = new WriteRegisterRequest(lr, signatureBase64, myServerName,
                        this.write_timestamp);
                WriteRegisterReply writeRegisterReply = writeRegisterClient.submitWriteRegisterRequest(writerRegisterRequest);

                boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForWriteReply(
                        serverName, writeRegisterReply.acknowledgment, writeRegisterReply.ts, writeRegisterReply.signatureBase64);

                if(!isSignatureCorrect) {
                    LOG.info("WRITE REGISTER REQUEST: Signature Validation of write reply Failed. We shall treat this as faulty behavior. Acknowledgment rejected");
                    continue; // Ignore the acknowledgment
                }

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

    private LocationReport getMostRecentLocationReport(int replicasNumber) {
        LocationReport locationReport = null;
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
        return locationReport;
    }

    private ArrayList<String> getMostRecentUsersAtPosition(int replicasNumber) {
        ArrayList<String> usersAtPosition = null;
        if(read_list_for_users_at_pos.size() > (replicasNumber + f) / 2){
            int max = 0;
            for(DataVersionForUsersAtPosition elem: read_list_for_users_at_pos){
                if(elem.getTS() > max){
                    max = elem.getTS();
                    usersAtPosition = elem.getData();
                }
            }
            read_list_for_users_at_pos.clear();
        }
        return usersAtPosition;
    }

    public LocationReport readSyncToGetLocationReport(int epoch, String username) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        LocationReport locationReport = null;
        int replicasNumber = AppLifecycleBean.location_servers.entrySet().size();
        read_list.clear();
        Iterator serversIterator = AppLifecycleBean.location_servers.entrySet().iterator();
        while (serversIterator.hasNext()) {
            Map.Entry server = (Map.Entry) serversIterator.next();
            String serverName = (String) server.getKey();
            String serverUrl = (String) server.getValue();
            String myServerName = System.getenv("SERVER_NAME");
            String signatureBase64 = signatureService.generateSha256WithRSASignatureForReadRequest(myServerName, this.rid);

            ReadRegisterClient readRegisterClient = RestClientBuilder.newBuilder()
                    .baseUri(new URI(serverUrl))
                    .build(ReadRegisterClient.class);
            ReadRegisterRequest readRegisterRequest = new ReadRegisterRequest(this.rid, myServerName, username, signatureBase64);
            ReadRegisterReply readRegisterReply = readRegisterClient.submitReadRegisterRequestToGetLocationReport(readRegisterRequest);

            boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForReadReply(
                    serverName, readRegisterReply.ts, readRegisterReply.rid, readRegisterReply.signatureBase64);

            if(!isSignatureCorrect) {
                LOG.info("READ REGISTER REQUEST: Signature Validation of read reply Failed. We shall treat this as faulty behavior. Acknowledgment rejected");
                continue; // Ignore the acknowledgment
            }

            locationReport = readRegisterReply.lr;

            DataVersion dv = new DataVersion(readRegisterReply.ts, locationReport);
            read_list.add(dv);
        }

        locationReport = getMostRecentLocationReport(replicasNumber);

        HashMap<Integer, LocationReport> locationReportAtEpochHashMap = new HashMap<>();
        locationReportAtEpochHashMap.put(epoch, locationReport);
        LocationReportsStorage.users.put(locationReport.username, locationReportAtEpochHashMap);

        return locationReport;
    }

    public ArrayList<String> readSyncToGetUserAtPosition(int x, int y, int epoch) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        ArrayList<String> usersAtPosition = null;
        int replicasNumber = AppLifecycleBean.location_servers.entrySet().size();
        read_list.clear();
        Iterator serversIterator = AppLifecycleBean.location_servers.entrySet().iterator();
        LOG.info("READ REGISTER Get user at position: Broadcasting read operation to all servers");
        while (serversIterator.hasNext()) {
            Map.Entry server = (Map.Entry) serversIterator.next();
            String serverName = (String) server.getKey();
            String serverUrl = (String) server.getValue();
            String myServerName = System.getenv("SERVER_NAME");
            String signatureBase64 = signatureService.generateSha256WithRSASignatureForReadRequest(myServerName, this.rid);

            LOG.info("READ REGISTER Get user at position: Broadcasting to server: " + serverUrl);
            ReadRegisterClient readRegisterClient = RestClientBuilder.newBuilder()
                    .baseUri(new URI(serverUrl))
                    .build(ReadRegisterClient.class);
            ReadRegisterRequest readRegisterRequest = new ReadRegisterRequest(this.rid, x, y, epoch, myServerName, signatureBase64);
            ReadRegisterReply readRegisterReply = readRegisterClient.submitReadRegisterRequestToGetUsersAtPosition(readRegisterRequest);

            LOG.info("READ REGISTER Get user at position: got reply from server: " + serverUrl);
            boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForReadReply(
                    serverName, readRegisterReply.ts, readRegisterReply.rid, readRegisterReply.signatureBase64);

            if(!isSignatureCorrect) {
                LOG.info("READ REGISTER REQUEST: Signature Validation of read reply Failed. We shall treat this as faulty behavior. Acknowledgment rejected");
                continue; // Ignore the acknowledgment
            }

            if(readRegisterReply.usersAtLocation != null) {
                ArrayList<String> usersAtLocation = readRegisterReply.usersAtLocation;

                DataVersionForUsersAtPosition dv = new DataVersionForUsersAtPosition(readRegisterReply.ts, usersAtLocation);
                read_list_for_users_at_pos.add(dv);
            }
        }

        try {
            usersAtPosition = getMostRecentUsersAtPosition(replicasNumber);
            return usersAtPosition;
        } catch(NullPointerException e) {
            return null;
        }
    }

    public String get_location_report(String username, int epoch, String signatureBase64) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        LocationReport lr = readSyncToGetLocationReport(epoch, username);
        System.out.println("LOCATION REPORT!!");
        System.out.println(LocationReportsStorage.users.toString());
        System.out.println("USERNAME " + username);
        System.out.println("EPOCH " + epoch);

        System.out.println("DONE");
        return String.format("User %s was at location x:%s y:%s", lr.username, lr.x, lr.y);
    }

    public String get_user_at(int x, int y, int epoch) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        ArrayList<String> users_at_loc = readSyncToGetUserAtPosition(x, y, epoch);
        if(users_at_loc != null) {
            String.format("READ Get user at: users at the location x:%d, y:%d, epoch:%d are: [%s]", x, y, epoch, users_at_loc.toString());
            return users_at_loc.toString();
        } else {
            return "Not found";
        }
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
