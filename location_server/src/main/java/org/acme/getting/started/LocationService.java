package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LocationService {
    private static final Logger LOG = Logger.getLogger(LocationService.class);
    private final ConcurrentHashMap<String, ConcurrentHashMap> users;

    @Inject
    SignatureService signatureService;

    public LocationService() {
        this.users = new ConcurrentHashMap<>();
    }

    public String submit_location_report(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        ConcurrentHashMap<Integer, LocationReport> location_reports = new ConcurrentHashMap<>();
        System.out.println("SAVING");
        System.out.println("EPOCH " + lr.epoch);
        System.out.println("USERNAME " + lr.username);
        System.out.println("BASE64  " + lr.signatureBase64);

        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignature(lr.username, lr.epoch, lr.x, lr.y, lr.replies, lr.signatureBase64);

        if(!isSignatureCorrect) {
            LOG.info("Signature Validation Failed. Aborting");
            return "Failed";
        }

        location_reports.put(lr.epoch, lr);
        users.put(lr.username, location_reports);

        return "Submitted";
    }

    public String get_location_report(String username, int epoch) {
        System.out.println("USERNAME " + username);
        System.out.println("EPOCH " + epoch);
        LocationReport lr;
        try{
            lr = (LocationReport) users.get(username).get(epoch);
        }catch (NullPointerException e){
            return "Not found";
        }
        System.out.println("DONE");
        return lr.username + lr.x + lr.y + lr.replies.toString();
    }
}
