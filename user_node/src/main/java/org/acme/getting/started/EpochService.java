package org.acme.getting.started;

import io.quarkus.runtime.Startup;
import org.acme.crypto.CryptoKeysUtil;
import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.*;
import org.acme.getting.started.resource.LocationServerClient;
import org.acme.getting.started.resource.ProofResourceClient;
import org.acme.getting.started.resource.SessionServerClient;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import org.acme.crypto.SessionService;

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
    public SessionService sessionService;
    Random ran = new Random();


    public EpochService(){

        try {

            sessionService = new SessionService();
            getServerSessionKey();
            LOG.info("Epoch service startup ");
            LOG.info("Session key is - " + sessionService.getSessionKey());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                epoch++;
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
        timer.scheduleAtFixedRate(repeatedTask, 0, 10000);

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

                        //LOG.info("deciphered location report bytes - " + Base64.getEncoder().encodeToString( LocationReport.toBytes(lr) ) );


                        Key sessionKey = sessionService.getSessionKey();
                        //LOG.info("Ciphering lr with session key - " + sessionKey);
                        CipheredLocationReport clr = sessionService.cipherLocationReport(sessionKey, lr);

                        LOG.info(clr.getUsername());
                        //LOG.info("ciphered location report bytes - " + Base64.getEncoder().encodeToString( clr.getCipheredLocationReportBytes() ) );

                        //sessionService.decipherLocationReport(sessionKey, clr);

                        LOG.info("Submitting location report");

                        LocationServerClient lsc = RestClientBuilder.newBuilder()
                                .baseUri(new URI("http://localhost:8080"))
                                .build(LocationServerClient.class);
                        lsc.submitLocationReport(clr);
                    }
                }
            }
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
                | IOException | CertificateException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public int get_epoch() {
        return epoch;
    }

    private Key getServerSessionKey() throws Exception{

        //SessionService sessionService = new SessionService();
        String my_username = System.getenv("USERNAME");

        //LOG.info("Getting key for user");
        //LOG.info(my_username);

        PrivateKey userPriv = CryptoKeysUtil.getPrivateKeyFromKeystore(my_username);
        SessionKeyRequest skr = new SessionKeyRequest(my_username, ran.nextInt());

        SignedSessionKeyRequest sskr = sessionService.signSessionKeyRequest(skr, userPriv);

        //LOG.info(sskr.toString());

        SessionServerClient ssc = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:8080"))
                .build(SessionServerClient.class);
        CipheredSessionKeyResponse response = ssc.submitSignedSessionKeyRequest(sskr);

        LOG.info("Session key request response - " + response);

        sessionService.handleCipheredSessionKeyResponse(response);


        return sessionService.getSessionKey();

    }



}
