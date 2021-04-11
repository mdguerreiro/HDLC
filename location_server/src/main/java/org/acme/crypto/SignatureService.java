package org.acme.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.runtime.Startup;

import org.acme.getting.started.LocationProofReply;
import org.acme.utils.Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;

@Startup
@Singleton
public class SignatureService {
    private static final Logger LOG = Logger.getLogger(SignatureService.class);
    final String keyStorePassword = "changeit";
    Util util = new Util();


    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writeValue(os, obj);

        return os.toByteArray();
    }

    private static byte[][] convertLocationProofRepliesArrayToBytes(ArrayList<LocationProofReply> replies) throws IOException, NoSuchAlgorithmException {
        byte[][] data = new byte[replies.size()][];
        for (int i = 0; i < replies.size(); i++) {
            LocationProofReply locationProofReply = replies.get(i);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] serializable = serialize(locationProofReply);
            byte[] digest = md5.digest(serializable);
            data[i] = digest;
        }

        return data;
    }

    private PublicKey getPublicKeyFromKeystore(String username) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        String keyAlias = username + "keyStore";
        String keyStoreLocation = "keys/" + username + "_key_store.p12";

        InputStream keyPairAsStream = util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());

        Certificate certificate = keyStore.getCertificate(keyAlias);

        return certificate.getPublicKey();
    }

    private PrivateKey getPrivateKeyFromKeystore(String username) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String keyAlias = username + "keyStore";
        String keyStoreLocation = "keys/" + username + "_key_store.p12";

        InputStream keyPairAsStream = util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());
        return (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
    }

    public boolean verifySha256WithRSASignature(String username, int xLoc, int yLoc, ArrayList<LocationProofReply>replies, String receivedSignatureBase64) throws NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, CertificateException, SignatureException {
        LOG.info(String.format("Validating Sha256 with RSA Signature"));

        PublicKey publicKey = getPublicKeyFromKeystore(username);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(xLoc).getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(yLoc).getBytes(StandardCharsets.UTF_8));

        byte[][] repliesByteArray = convertLocationProofRepliesArrayToBytes(replies);

        for(int i = 0; i < repliesByteArray.length; i++) {
            signature.update(repliesByteArray[i]);
        }

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

        PrivateKey privateKey = getPrivateKeyFromKeystore(username);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(username.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(status).getBytes(StandardCharsets.UTF_8));

        return signature.sign();
    }
}

//        String base64DigitalSignature = Base64.getEncoder().encodeToString(digitalSignature);
//        System.out.println("Base64: " + base64DigitalSignature);
