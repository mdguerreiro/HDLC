package org.acme.getting.started;

import io.quarkus.runtime.Startup;
import org.acme.crypto.SignatureService;
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


import org.acme.getting.started.data.*;


import javax.inject.Inject;
import javax.inject.Singleton;


@Startup
@Singleton
public class HealthAuthorityService {
    private static final Logger LOG = Logger.getLogger(HealthAuthorityService.class);
    public SessionService sessionService;
    Random ran = new Random();


    public HealthAuthorityService(){

        try {

            sessionService = new SessionService();
            getServerSessionKey();
            LOG.info("HealthAuthrotiy startup ");
            LOG.info("Session key is - " + sessionService.getSessionKey());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, 30000);

    }

    @Inject
    SignatureService signatureService;

    @ConfigProperty(name = "location.server.url")
    String l_srv_url;


    private Key getServerSessionKey() throws Exception{

        //SessionService sessionService = new SessionService();
        String my_username = System.getenv("health_authority");

        //LOG.info("Getting key for user");
        //LOG.info(my_username);

        PrivateKey userPriv = sessionService.getPrivateKeyFromKeystore(my_username);
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
