package org.acme.crypto;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;

import java.io.InputStream;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import java.security.*;

import java.io.IOException;

import org.acme.utils.Util;

import java.nio.charset.StandardCharsets;

import org.acme.getting.started.SessionKeyRequest;
import org.acme.getting.started.SignedSessionKeyRequest;
import org.acme.getting.started.CipheredSessionKeyResponse;
import org.acme.getting.started.CipheredLocationReport;
import org.acme.getting.started.LocationReport;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;


import java.util.Base64;

@Startup
@Singleton
public class SessionService {

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


    public SignedSessionKeyRequest signSessionKeyRequest( SessionKeyRequest skr, PrivateKey privKey ) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privKey);

        signature.update(skr.getUserId().getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(skr.getNonce()).getBytes(StandardCharsets.UTF_8));

        return new SignedSessionKeyRequest(skr, signature.sign());

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


    public PublicKey getPublicKeyFromKeystore(String username, String keyAlias) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
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

    public Key decipherSessionKeyResponse( CipheredSessionKeyResponse cskr, PrivateKey userKey){

        byte[] cipheredAESKeyBytes = cskr.getCipheredAESKeyBytes();

        return null;
    }


    public boolean verifyCipheredSessionKeyResponse( CipheredSessionKeyResponse cskr) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException{

        byte[] cipheredKeyBytes = cskr.getCipheredAESKeyBytes();
        byte[] serverSignature = cskr.getServerSignature();

        LOG.info("veryfiying cskr ----------------");
        LOG.info(cskr.toString());
        LOG.info("-----------------------------------------------------");



        PublicKey serverPub = getPublicKeyFromKeystore("location_server", "locationServerKeyStore");

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(serverPub);

        signature.update(cipheredKeyBytes);

        return signature.verify(serverSignature);
    }


    public Key decipherSessionKey(byte[] cipheredSessionKeyBytes, PrivateKey userPriv) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException{

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, userPriv);

        //decipher the AES session key using the user's private key
        byte[] decipheredSessionKeyBytes = cipher.doFinal(cipheredSessionKeyBytes);

        Key sessionKey = new SecretKeySpec(decipheredSessionKeyBytes, 0, decipheredSessionKeyBytes.length, "AES");


        return sessionKey;
    }


    public Key handleCipheredSessionKeyResponse( CipheredSessionKeyResponse cskr ) {
        try {
            PublicKey serverPub = getPublicKeyFromKeystore("location_server", "locationServerKeyStore");
        }
        catch(Exception e){
            LOG.info("Erorr loading server public key");
            return null;
        }

        try {

            boolean validSignature = verifyCipheredSessionKeyResponse(cskr);

            if(validSignature){
                LOG.info("Server sent a valid Session key response");
            }
            else{
                LOG.info("Server sent an invalid Session key response");
            }

            PrivateKey userPriv = getPrivateKeyFromKeystore(System.getenv("USERNAME"));
            Key sessionKey = decipherSessionKey( cskr.getCipheredAESKeyBytes(), userPriv );

            LOG.info("Session Key deciphered - " + Base64.getEncoder().encodeToString(sessionKey.getEncoded()) );
            return sessionKey;

        }
        catch(Exception e){
            e.printStackTrace();
        }

       return null;

    }


    public CipheredLocationReport cipherLocationReport(Key sessionKey, LocationReport lr){

        try {
            byte[] locationReportBytes = LocationReport.toBytes(lr);
            Cipher cipher = Cipher.getInstance("AES/EBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey);

            byte[] cipheredLocationReportBytes = cipher.doFinal();

            return new CipheredLocationReport(lr.username, cipheredLocationReportBytes);
        }
        catch(Exception e){
            e.printStackTrace();

            return new CipheredLocationReport("error", (new String("error").getBytes() ) );
        }

    }


    public LocationReport decipherLocationReport(Key sessionKey, CipheredLocationReport clr){

        try {

            Cipher cipher = Cipher.getInstance("AES/EBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sessionKey);

            byte[] cipheredLocationReportBytes = clr.getCipheredLocationReportBytes();
            byte[] locationReportBytes = cipher.doFinal();

            return LocationReport.fromBytes(locationReportBytes);
        }

        catch(Exception e){
            LOG.info("Error decrypting ciphered location report");
            e.printStackTrace();

        }

        return null;

    }



}