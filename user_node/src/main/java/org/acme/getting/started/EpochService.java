package org.acme.getting.started;

import io.quarkus.runtime.Startup;
import org.acme.crypto.SignatureService;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;


@Startup
@Singleton
public class EpochService {
    private static final Logger LOG = Logger.getLogger(EpochService.class);
    private int epoch = -1;

    public EpochService(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                epoch++;
                if(epoch == 5) {
                    try {
                        Thread.sleep(1000);
                        System.exit(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    request_location_proof();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, 15000);

    }

    @Inject
    SignatureService signatureService;

    @ConfigProperty(name = "location.server.url")
    String l_srv_url;

    private void request_location_proof() throws URISyntaxException {
        int epoch = get_epoch();
        String my_username = System.getenv("USERNAME");
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(epoch).get(my_username);
        Iterator it = AppLifecycleBean.epochs.get(epoch).entrySet().iterator();
        try {
            String signatureBase64 = signatureService.generateSha256WithRSASignatureForLocationRequest(my_username, my_Loc.get_X(), my_Loc.get_Y());

            LocationProofRequest lpr = new LocationProofRequest(my_username, my_Loc.get_X(), my_Loc.get_Y(), signatureBase64);
            ArrayList<LocationProofReply> replies = new ArrayList<>();
            while(it.hasNext()){
                Map.Entry elem = (Map.Entry)it.next();
                Location l = (Location) elem.getValue();
                if(!elem.getKey().equals(my_username)){
                    if(AppLifecycleBean.is_Close(my_Loc, l)){
                        LOG.info(String.format("Process %s found %s close at epoch %d. Starting broadcast.", my_username,
                                elem.getKey(), epoch));
                        LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", my_username, my_Loc.get_X(),
                                my_Loc.get_Y()));
                        LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", elem.getKey(), l.get_X(),
                                l.get_Y()));
                        String hostname = AppLifecycleBean.hosts.get(elem.getKey());
                        ProofResourceClient prc = RestClientBuilder.newBuilder()
                                .baseUri(new URI(hostname))
                                .build(ProofResourceClient.class);

                        LocationProofReply lp_reply = null;
                        try{
                            lp_reply = prc.proof_request(lpr);
                        }
                        catch(Exception e){
                            LOG.error(String.format("Couldn't connect to node %s to perform request. Check if it is online", hostname));
                            break;
                        }

                        boolean isSignatureCorrect = signatureService.verifySha256WithRSASignatureForLocationRequest(lp_reply.status, lp_reply.signer, lp_reply.signatureBase64);

                        if(isSignatureCorrect) {
                            LOG.info(String.format("Signature is correct. Adding the correct reply: '%s' to the list of replies", lp_reply.signer));
                            replies.add(lp_reply);
                        }

                        String signatureBase64ForLocationReport = signatureService.generateSha256WithRSASignatureForLocationReport(my_username, epoch, my_Loc.get_X(), my_Loc.get_Y(), replies);
                        System.out.println("BASE64  " + signatureBase64);

                        LocationReport lr = new LocationReport(my_username, epoch, my_Loc.get_X(), my_Loc.get_Y(), replies, signatureBase64ForLocationReport);
                        LocationServerClient lsc = RestClientBuilder.newBuilder()
                                .baseUri(new URI(l_srv_url))
                                .build(LocationServerClient.class);
                        lsc.submitLocationReport(lr);
                    }
                }
            }
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
                | IOException | CertificateException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public int get_epoch() {
        //LOG.info("Returning epoch time from service: " + epoch);
        return epoch;
    }

}
