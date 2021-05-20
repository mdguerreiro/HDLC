package org.ha.getting.started;
import org.ha.getting.started.model.*;
import org.ha.crypto.SignatureService;
import org.ha.getting.started.model.ObtainLocationRequest;

import javax.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

import org.jboss.logging.Logger;


import java.util.Base64;

class HealthAuthorityService{

    private static final Logger LOG = Logger.getLogger(HealthAuthorityService.class);

    @Inject
    SignatureService signatureService;

    private String serverId;

    private PrivateKey haPrivateKey;
    private PublicKey serverPubKey;


    public HealthAuthorityService(String haId,String serverId){

        this.serverId = serverId;

        try {
            haPrivateKey = signatureService.getPrivateKeyFromKeystore(haId);
            serverPubKey = signatureService.getPublicKeyFromKeystore(serverId);
        }
        catch(Exception e){
            //
        }
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




}