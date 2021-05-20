package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.*;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Singleton
public class ReadRegisterService {
    private static final Logger LOG = Logger.getLogger(ReadRegisterService.class);

    @Inject
    LocationService locationService;

    @Inject
    SignatureService signatureService;

    public ReadRegisterService() {

    }


    public ReadRegisterReply replayReadRegisterWithSignature(ReadRegisterReply readRegisterReply) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String myServerName = System.getenv("SERVER_NAME");

        String signatureBase64 = signatureService.generateSha256WithRSASignatureForReadReply(
                myServerName, readRegisterReply.ts, readRegisterReply.rid);
        readRegisterReply.signatureBase64 = signatureBase64;
        return readRegisterReply;
    }

    public ReadRegisterReply submitReadRegisterRequest(ReadRegisterRequest readRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForReadRequest(
                readRegisterRequest.senderServerName, readRegisterRequest.rid, readRegisterRequest.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("READ REGISTER REPLY: Signature Validation Failed. Aborting");
            WriteRegisterReply writeRegisterReply = new WriteRegisterReply();
            writeRegisterReply.acknowledgment = "false";
            return new ReadRegisterReply();
        }

        String myServerName = System.getenv("SERVER_NAME");
        int epoch = readRegisterRequest.epoch;
        String username = readRegisterRequest.username;

        LocationReport lr;
        try {
            lr = locationService.users.get(username).get(epoch);
        } catch (NullPointerException e) {
            lr = null;
        }
        ReadRegisterReply readRegisterReply = new ReadRegisterReply();
        readRegisterReply.senderServerName = myServerName;
        readRegisterReply.ts = locationService.data_ts;
        readRegisterReply.rid = readRegisterRequest.rid;
        readRegisterReply.lr = lr;
        LOG.info("READ REQUEST MADE - SENDING REPLY TO READER SERVER");
        return replayReadRegisterWithSignature(readRegisterReply);
    }
}
