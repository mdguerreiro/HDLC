package org.acme.crypto;

import io.quarkus.runtime.Startup;

import org.acme.getting.started.model.LocationProofReply;
import org.acme.getting.started.model.ha.ObtainUserAtLocationRequest;

import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;

@Startup
@Singleton
public class SignatureService {
    private static final Logger LOG = Logger.getLogger(SignatureService.class);

    public boolean verifySha256WithRSASignature(String username, int epoch, int xLoc, int yLoc, ArrayList<LocationProofReply> replies, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("Validating Sha256 with RSA Signature"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(username);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(epoch).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(xLoc).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(yLoc).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public byte[] generateSha256WithRSASignature(String username, String status) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("Generating Sha256 with RSA Signature"));
        LOG.info(String.format("%s Location Reply status -> %s", username, status));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(username);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(status).getBytes(StandardCharsets.UTF_8));

        return signature.sign();
    }

    public static boolean verifySignature(ObtainUserAtLocationRequest request) throws Exception{

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


        byte[] requestSignature = Base64.decodeBase64( request.getHaSignature() );
        boolean isValid = signature.verify(requestSignature);

        return isValid;


    }



}
