package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;

import java.io.InputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.KeyStore;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;


import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;

import org.acme.utils.Util;


import java.nio.charset.StandardCharsets;

import org.acme.getting.started.SessionKeyRequest;
import org.acme.getting.started.SignedSessionKeyRequest;
import org.acme.getting.started.CipheredSessionKeyResponse;


@ApplicationScoped
public class ServerSessionService {

    private static final Logger LOG = Logger.getLogger(SignatureService.class);
    final String keyStorePassword = "changeit";
    Util util = new Util();

    public byte[] generateAESSessionKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        System.out.println("Finish generating AES key");
        byte[] encoded = key.getEncoded();

        return encoded;

    }

    public PublicKey getPublicKeyFromKeystore(String username) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        String keyAlias = username + "keyStore";
        String keyStoreLocation = "keys/" + username + "_key_store.p12";

        InputStream keyPairAsStream = util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());

        Certificate certificate = keyStore.getCertificate(keyAlias);

        return certificate.getPublicKey();
    }


    public CipheredSessionKeyResponse handleSignedSessionKeyRequest( SignedSessionKeyRequest sskr ) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException{

        LOG.info("received session key request ------------------------");
        LOG.info(sskr.toString());
        LOG.info("ssk END -------------------------");
        String userId = sskr.getSessionKeyRequest().getUserId();
        int nonce =  sskr.getSessionKeyRequest().getNonce();

        PublicKey userPub = getPublicKeyFromKeystore(userId);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(userPub);

        signature.update(userId.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(nonce).getBytes(StandardCharsets.UTF_8));

        boolean validSignature = signature.verify( sskr.getSignature() );

        if( validSignature) {
            LOG.info("Valid Signature from -  " + userId);

        }
        else{
            LOG.info("Invalid Signature from - " + userId);
        }
        return null;
    }

}