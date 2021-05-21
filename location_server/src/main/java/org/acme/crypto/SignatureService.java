package org.acme.crypto;

import io.quarkus.runtime.Startup;

import org.acme.getting.started.model.LocationProofReply;
import org.acme.getting.started.model.LocationReport;
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

    public boolean verifySha256WithRSASignatureForWriteRequest(String serverName, LocationReport locationReport, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("WRITE REGISTER REQUEST: Validating Sha256 with RSA Signature for write register"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(serverName);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.epoch).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.x).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.y).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("WRITE REGISTER REQUEST: Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("WRITE REGISTER REQUEST: Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public String generateSha256WithRSASignatureForWriteRequest(String serverName, LocationReport locationReport) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("WRITE REGISTER REQUEST: Generating Sha256 with RSA Signature"));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(serverName);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.epoch).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.x).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(locationReport.y).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return java.util.Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public String generateSha256WithRSASignatureForWriteReply(String serverName, String acknowledgment, int ts) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, InvalidKeyException, SignatureException {
        LOG.info(String.format("WRITE REGISTER REPLY: Generating Sha256 with RSA Signature"));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(serverName);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(acknowledgment.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(ts).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return java.util.Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public boolean verifySha256WithRSASignatureForWriteReply(String serverName, String acknowledgment, int ts, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("WRITE REGISTER REPLY: Validating Sha256 with RSA Signature for write reply"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(serverName);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(acknowledgment.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(ts).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("WRITE REGISTER REPLY: Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("WRITE REGISTER REPLY: Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public String generateSha256WithRSASignatureForReadRequest(String serverName, int rid) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException, UnrecoverableKeyException {
        LOG.info(String.format("READ REGISTER REQUEST: Generating Sha256 with RSA Signature"));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(serverName);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(rid).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return java.util.Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public boolean verifySha256WithRSASignatureForReadRequest(String serverName, int rid, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("READ REGISTER REQUEST: Validating Sha256 with RSA Signature for read register"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(serverName);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(rid).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("READ REGISTER REQUEST: Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("READ REGISTER REQUEST: Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }

    public String generateSha256WithRSASignatureForReadReply(String serverName, int ts, int rid) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException, UnrecoverableKeyException {
        LOG.info(String.format("READ REGISTER REPLY: Generating Sha256 with RSA Signature"));

        PrivateKey privateKey = CryptoKeysUtil.getPrivateKeyFromKeystore(serverName);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(ts).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(rid).getBytes(StandardCharsets.UTF_8));

        byte[] signatureByteArray = signature.sign();

        return java.util.Base64.getEncoder().encodeToString(signatureByteArray);
    }

    public boolean verifySha256WithRSASignatureForReadReply(String serverName, int ts, int rid, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException, UnrecoverableKeyException {
        LOG.info(String.format("READ REGISTER REPLY: Validating Sha256 with RSA Signature for read register reply"));

        PublicKey publicKey = CryptoKeysUtil.getPublicKeyFromKeystore(serverName);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(serverName.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(ts).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(rid).getBytes(StandardCharsets.UTF_8));

        byte[] receivedSignature = Base64.decodeBase64(receivedSignatureBase64);
        boolean isValidSignature = signature.verify(receivedSignature);

        if(isValidSignature) {
            LOG.info("READ REGISTER REQUEST: Signature Validation was performed successfully. Signature is valid");
        } else {
            LOG.info("READ REGISTER REQUEST: Signature Validation failed. Signature is invalid");
        }

        return isValidSignature;
    }
}
