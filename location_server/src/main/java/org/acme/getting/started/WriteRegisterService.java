package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegiterRequest;
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

    public WriteRegisterService() {

    }

    public WriteRegisterReply replyWriteRegisterWithSignature(WriteRegisterReply writeRegisterReply) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String myServerName = System.getenv("SERVER_NAME");

        /** TODO Fix-me */
        int value = 1;

        String signatureBase64 = signatureService.generateSha256WithRSASignatureForWriteRegister(myServerName, value);
        writeRegisterReply.signatureBase64 = signatureBase64;
        return writeRegisterReply;
    }

    public WriteRegisterReply submitWriteRegisterRequest(WriteRegiterRequest writeRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForWriteRegister(
                writeRegisterRequest.senderServerName, writeRegisterRequest.value, writeRegisterRequest.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("Signature Validation Failed. Aborting");
            return new WriteRegisterReply();
        }

        String OK = "OK";
        LOG.info("Submitting Write Register Request " + OK);
        return replyWriteRegisterWithSignature(new WriteRegisterReply());
    }
}
