package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;

import java.io.InputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import java.security.*;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;

import org.acme.utils.Util;

import java.nio.charset.StandardCharsets;

import org.acme.getting.started.SessionKeyRequest;
import org.acme.getting.started.SignedSessionKeyRequest;
import org.acme.getting.started.CipheredSessionKeyResponse;

import java.util.Base64;


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

        LOG.info("Plain Session Key generated  - " + Base64.getEncoder().encodeToString(key.getEncoded()) );

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

    public PrivateKey getPrivateKeyFromKeystore(String username) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String keyAlias = username + "keyStore";
        String keyStoreLocation = "keys/" + username + "_key_store.p12";

        InputStream keyPairAsStream = util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());
        return (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
    }

    public PrivateKey getPrivateKeyFromKeystore(String username, String keyAlias) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String keyStoreLocation = "keys/" + username + "_key_store.p12";

        InputStream keyPairAsStream = util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());
        return (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
    }


    public CipheredSessionKeyResponse handleSignedSessionKeyRequest( SignedSessionKeyRequest sskr ) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, UnrecoverableKeyException, BadPaddingException{

        //LOG.info("received session key request ------------------------");
        //LOG.info(sskr.toString());
        //LOG.info("ssk END -------------------------");

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
            return null;
        }

        //generate session key for the user
        byte[] AESKeyBytes = generateAESSessionKey();

        //cipher AES session key with the public key of the user
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, userPub);

        byte[] cipheredAESKeyBytes = cipher.doFinal(AESKeyBytes);


        //sign the ciphered session key with the private key of the server
        PrivateKey serverPriv;
        try {
             serverPriv = getPrivateKeyFromKeystore("location_server", "locationServerkeyStore");
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        Signature serverSignature = Signature.getInstance("SHA256withRSA");

        try {
            serverSignature.initSign(serverPriv);
            serverSignature.update(cipheredAESKeyBytes);
            byte[] serverSignatureBytes = serverSignature.sign();

            CipheredSessionKeyResponse cskr = new CipheredSessionKeyResponse(cipheredAESKeyBytes, serverSignatureBytes);
            LOG.info("Sending cskr ----------------");
            LOG.info(cskr.toString());
            LOG.info("-----------------------------------------------------");

            return cskr;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

}