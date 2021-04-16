package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;


@RequestScoped
public class ProofService {
    private static final Logger LOG = Logger.getLogger(ProofService.class);

    public ProofService(){
    }

    @Inject
    EpochService es;

    @Inject
    SignatureService signatureService;

    public LocationProofReply location_proof_request(LocationProofRequest lpr) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String my_username = System.getenv("USERNAME");
        String status = "DENIED";
        String signatureBase64;
        String isByzantine = System.getenv("isByzantine");
        try {
            boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForLocationReply(lpr.username, lpr.xLoc, lpr.yLoc, lpr.signatureBase64);

            if(!isSignatureCorrect) {
                LOG.info("Signature Validation Failed. Sending LP Reply to " + lpr.username);
                signatureBase64 = signatureService.generateSha256WithRSASignatureForLocationReply(my_username, status);
                return new LocationProofReply(status, my_username, signatureBase64);
            }

            int epoch = es.get_epoch();
            LOG.info(String.format("LPR received from %s at epoch %d", lpr.username, epoch));
            Location my_Loc = (Location) AppLifecycleBean.epochs.get(epoch).get(my_username);
            Location l = new Location(lpr.xLoc, lpr.yLoc);
            LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", my_username, my_Loc.get_X(), my_Loc.get_Y()));
            LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", lpr.username, l.get_X(), l.get_Y()));
            if (!AppLifecycleBean.is_Close(my_Loc, l)){
                LOG.error("LPR Received from " + lpr.username + " has invalid location.");
                signatureBase64 = signatureService.generateSha256WithRSASignatureForLocationReply(my_username, status);
                return new LocationProofReply(status, my_username, signatureBase64);
            }
            LOG.info("Location confirmed. Sending LP Reply to " + lpr.username);
            status = "APPROVED";
            if(isByzantine.equals("true")){
                status = "DENIED";
            }
            signatureBase64 = signatureService.generateSha256WithRSASignatureForLocationReply(my_username, status);
            return new LocationProofReply(status, my_username, signatureBase64);
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | InvalidKeyException | CertificateException | SignatureException | UnrecoverableKeyException e) {
            e.printStackTrace();
            LOG.info("Signature Validation Failed. Sending LP Reply to " + lpr.username);
            signatureBase64 = signatureService.generateSha256WithRSASignatureForLocationReply(my_username, status);
            return new LocationProofReply(status, my_username, signatureBase64);
        }
    }
}
