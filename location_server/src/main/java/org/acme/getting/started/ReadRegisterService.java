package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.*;
import org.acme.getting.started.storage.LocationReportsStorage;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Singleton
public class ReadRegisterService {
    private static final Logger LOG = Logger.getLogger(ReadRegisterService.class);

    @Inject
    LocationService locationService;

    @Inject
    SignatureService signatureService;

    public ReadRegisterService() {

    }


    public ReadRegisterReply replyReadRegisterWithSignature(ReadRegisterReply readRegisterReply) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String myServerName = System.getenv("SERVER_NAME");

        String signatureBase64 = signatureService.generateSha256WithRSASignatureForReadReply(
                myServerName, readRegisterReply.ts, readRegisterReply.rid);
        readRegisterReply.signatureBase64 = signatureBase64;
        return readRegisterReply;
    }

    private ArrayList<String> getUsersAtLocation(int x, int y, int epoch) {
        ArrayList<String> users_at_loc = new ArrayList<>();
        try {
            Iterator it = LocationReportsStorage.users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                LocationReport lr = LocationReportsStorage.users.get(pair.getKey()).get(epoch);
                if (lr.x == x && lr.y == y) {
                    users_at_loc.add((String) pair.getKey());
                }
            }
            return users_at_loc;
        } catch (NullPointerException e){
            return null;
        }
    }

    public ReadRegisterReply submitReadRegisterRequestToGetUsersAtPosition(ReadRegisterRequest readRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForReadRequest(
                readRegisterRequest.senderServerName, readRegisterRequest.rid, readRegisterRequest.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("READ REGISTER REPLY: Signature Validation Failed. Aborting");
            return new ReadRegisterReply();
        }

        String myServerName = System.getenv("SERVER_NAME");

        ArrayList<String> usersAtLocation = getUsersAtLocation(readRegisterRequest.x, readRegisterRequest.y, readRegisterRequest.epoch);

        ReadRegisterReply readRegisterReply = new ReadRegisterReply();
        readRegisterReply.senderServerName = myServerName;
        readRegisterReply.ts = locationService.data_ts;
        readRegisterReply.rid = readRegisterRequest.rid;
        readRegisterReply.usersAtLocation = usersAtLocation;
        LOG.info("READ REQUEST MADE - SENDING REPLY TO READER SERVER");
        return replyReadRegisterWithSignature(readRegisterReply);
    }

    public ReadRegisterReply submitReadRegisterRequestToGetLocationReport(ReadRegisterRequest readRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForReadRequest(
                readRegisterRequest.senderServerName, readRegisterRequest.rid, readRegisterRequest.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("READ REGISTER REPLY: Signature Validation Failed. Aborting");
            return new ReadRegisterReply();
        }

        String myServerName = System.getenv("SERVER_NAME");
        int epoch = readRegisterRequest.epoch;
        String username = readRegisterRequest.username;

        LocationReport lr;
        try {
            lr = LocationReportsStorage.users.get(username).get(epoch);
        } catch (NullPointerException e) {
            lr = null;
        }
        ReadRegisterReply readRegisterReply = new ReadRegisterReply();
        readRegisterReply.senderServerName = myServerName;
        readRegisterReply.ts = locationService.data_ts;
        readRegisterReply.rid = readRegisterRequest.rid;
        readRegisterReply.lr = lr;
        LOG.info("READ REQUEST MADE - SENDING REPLY TO READER SERVER");
        return replyReadRegisterWithSignature(readRegisterReply);
    }
}
