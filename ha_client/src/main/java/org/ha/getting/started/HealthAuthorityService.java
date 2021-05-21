package org.ha.getting.started;


import org.ha.getting.started.model.*;
import org.ha.crypto.*;

import org.ha.getting.started.model.ObtainLocationRequest;
import org.ha.getting.started.model.ObtainUserAtLocationRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import org.jboss.logging.Logger;

import io.quarkus.runtime.Startup;

import java.util.Base64;

import org.ha.getting.started.resource.LocationServerClient;

import java.util.Random;



@Startup
@Singleton
class HealthAuthorityService{

    private static final Logger LOG = Logger.getLogger(HealthAuthorityService.class);

    private String serverId;
    private String serverUrl;

    private PrivateKey haPrivateKey;
    private PublicKey serverPubKey;

    Random ran = new Random();

    public HealthAuthorityService(String haId,String serverId){

        LOG.info(serverId + " " + haId);
        serverUrl = "http://localhost:8080";

        this.serverId = serverId;

        try {
            haPrivateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(haId);
            serverPubKey = CryptoKeysUtil.getPublicKeyFromKeystore(serverId);
        }
        catch(Exception e){
            //
            haPrivateKey = null;
            serverPubKey = null;
            LOG.info("ERROR LOADING KEYS");
            e.printStackTrace();
        }

        ObtainUserAtLocationRequest request = new ObtainUserAtLocationRequest(
                0, //x
                0, //y
                ran.nextInt(), //nonce
                haId //health authority id
        );
        try {
            signObtainUserAtLocationRequest(request);
        }
        catch(Exception e){
            LOG.info("Error signing ObtainUserAtLocationRequest ");
        }

        try {
            sendObtainUserAtLocationRequest(request);
        }
        catch(Exception e){
            LOG.info("Error sending Request with signature " + request.getHaSignature() );
            e.printStackTrace();
        }
    }


    public HealthAuthorityService(){
        //CREATE HA PUB AND PRIV AND CHANGE TO HaID="ha{haId}"
        this("user5", "location_server_8080");
    }

     public void signObtainLocationRequest(ObtainLocationRequest request) throws Exception{

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(haPrivateKey);

        signature.update(request.getUserId().getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getEpoch()).getBytes(StandardCharsets.UTF_8));
        signature.update(request.getHaId().getBytes(StandardCharsets.UTF_8));



        byte[] signatureByteArray = signature.sign();
        request.setSignature(Base64.getEncoder().encodeToString(signatureByteArray));

     }


    public void signObtainUserAtLocationRequest(ObtainUserAtLocationRequest request) throws Exception{

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(haPrivateKey);

        signature.update(String.valueOf(request.getX()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getY()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getEpoch()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getNonce()).getBytes(StandardCharsets.UTF_8));
        signature.update(request.getHaId().getBytes(StandardCharsets.UTF_8));



        byte[] signatureByteArray = signature.sign();
        request.setSignature(Base64.getEncoder().encodeToString(signatureByteArray));

    }


     public boolean verifySignature(ObtainLocationRequest request) throws Exception{

         //CREATE HA PUB AND PRIV AND CHANGE TO HaID="ha{haId}"
        String haId = "user5";
        PublicKey haPublicKey = CryptoKeysUtil.getPublicKeyFromKeystore(haId);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(haPublicKey);

        signature.update(request.getUserId().getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getEpoch()).getBytes(StandardCharsets.UTF_8));
        signature.update(request.getHaId().getBytes(StandardCharsets.UTF_8));


        byte[] requestSignature = Base64.getDecoder().decode( request.getHaSignature() );
        boolean isValid = signature.verify(requestSignature);

        return isValid;


     }

    public boolean verifySignature(ObtainUserAtLocationRequest request) throws Exception{

        //CREATE HA PUB AND PRIV AND CHANGE TO HaID="ha{haId}"
        String haId = "user5";
        PublicKey haPublicKey = CryptoKeysUtil.getPublicKeyFromKeystore(haId);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(haPublicKey);

        signature.update(String.valueOf(request.getX()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getY()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getEpoch()).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(request.getNonce()).getBytes(StandardCharsets.UTF_8));
        signature.update(request.getHaId().getBytes(StandardCharsets.UTF_8));


        byte[] requestSignature = Base64.getDecoder().decode( request.getHaSignature() );
        boolean isValid = signature.verify(requestSignature);

        return isValid;


    }



    public void sendObtainUserAtLocationRequest(ObtainUserAtLocationRequest request) throws Exception {

        LOG.info("Submitting Obtain User Location At to the " + serverUrl);
        LOG.info("Signature - "+ request.getHaSignature());
        LocationServerClient lsc = RestClientBuilder.newBuilder()
                .baseUri(new URI(serverUrl))
                .build(LocationServerClient.class);
        String response = lsc.obtainUsersAtLocation(request);
        LOG.info(String.format("Obtain User Location At response - {%s}", response));


    }

    
}