package org.acme.crypto;

import io.quarkus.runtime.Startup;

import org.acme.getting.started.model.LocationProofReply;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;


@Startup
@Singleton
public class SignatureService {
    private static final Logger LOG = Logger.getLogger(SignatureService.class);

    public boolean verifySha256WithRSASignatureForLocationReply(String username, int xLoc, int yLoc, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("Validating Sha256 with RSA Signature for LocationReply"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(username);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(xLoc).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(yLoc).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = org.apache.commons.codec.binary.Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public boolean verifySha256WithRSASignatureForLocationRequest(String status, String username, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("Validating Sha256 with RSA Signature for LocationRequest"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(username);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(status).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = org.apache.commons.codec.binary.Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public String generateSha256WithRSASignatureForLocationRequest(String username, int xLoc, int yLoc) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("Generating Sha256 with RSA Signature for LocationRequest"));
        LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", username, xLoc,
                yLoc));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(username);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(xLoc).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(yLoc).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public String generateSha256WithRSASignatureForLocationReport(String username, int epoch, int xLoc, int yLoc, ArrayList<LocationProofReply> replies) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("Generating Sha256 with RSA Signature for LocationReport"));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(username);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(epoch).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(xLoc).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(yLoc).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public String generateSha256WithRSASignatureForLocationReply(String username, String status) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("Generating Sha256 with RSA Signature for LocationReply"));
        LOG.info(String.format("%s Location Reply status -> %s", username, status));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(username);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(status).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return Base64.getEncoder().encodeToString(signatureByteArray);
    }
}