package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;
import org.acme.getting.started.storage.LocationReportsStorage;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Singleton
public class WriteRegisterService {
    private static final Logger LOG = Logger.getLogger(WriteRegisterService.class);

    @Inject
    SignatureService signatureService;

    @Inject
    LocationService locationService;

    public WriteRegisterService() {

    }

    public WriteRegisterReply replyWriteRegisterWithSignature(WriteRegisterReply writeRegisterReply) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String myServerName = System.getenv("SERVER_NAME");

        String signatureBase64 = signatureService.generateSha256WithRSASignatureForWriteReply(myServerName, writeRegisterReply.acknowledgment, writeRegisterReply.ts);
        writeRegisterReply.signatureBase64 = signatureBase64;
        return writeRegisterReply;
    }

    public WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest writeRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForWriteRequest(
                writeRegisterRequest.senderServerName, writeRegisterRequest.locationReport, writeRegisterRequest.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("WRITE REGISTER REPLY: Signature Validation Failed. Aborting");
            WriteRegisterReply writeRegisterReply = new WriteRegisterReply();
            writeRegisterReply.acknowledgment = "false";
            return new WriteRegisterReply();
        }

        if(writeRegisterRequest.wts > locationService.data_ts){
            locationService.data_ts = writeRegisterRequest.wts;
            locationService.validateLocationReport(writeRegisterRequest.locationReport);
        }

        LOG.info("Submitting Write Register Request ");
        WriteRegisterReply writeRegisterReply = new WriteRegisterReply("true", writeRegisterRequest.wts);

        return replyWriteRegisterWithSignature(writeRegisterReply);
    }
}
