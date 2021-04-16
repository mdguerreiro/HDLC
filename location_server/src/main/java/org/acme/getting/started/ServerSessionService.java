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
import java.util.HashMap;


@ApplicationScoped
public class ServerSessionService {

    private static final Logger LOG = Logger.getLogger(SignatureService.class);
    final String keyStorePassword = "changeit";
    Util util = new Util();
    private static HashMap<String,Key> keyOfUser = new HashMap<String,Key>();
    private static HashMap<String,HashMap> usedNoncesOfUser = new HashMap<String,HashMap>();


    public byte[] generateAESSessionKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        //System.out.println("Finish generating AES key");
        byte[] encoded = key.getEncoded();

        //LOG.info("Plain Session Key generated  - " + Base64.getEncoder().encodeToString(key.getEncoded()) );

        return encoded;

    }

    public static void setUserSessionKey(String userId, byte[] sessionKeyBytes){
        Key sessionKey = new SecretKeySpec(sessionKeyBytes, 0, sessionKeyBytes.length, "AES");
        keyOfUser.put(userId, sessionKey);

        LOG.info("\n");
        LOG.info("setUserssionKey -------------------------");
        LOG.info("setting session key of user - " + userId);
        LOG.info("session key of user - " + Base64.getEncoder().encodeToString(sessionKey.getEncoded()) );
        LOG.info("session key in the hashmap is " + getUserSessionKey(userId));
        LOG.info("setUserssionKey END -------------------------");
        LOG.info("\n");

    }

    public static Key getUserSessionKey(String userId){
        LOG.info("\n");
        LOG.info("----------getusersessionkey()----------------");
        LOG.info("getting key in the hashmap for user - " + userId);
        LOG.info("session key in the hashmap is " + ServerSessionService.keyOfUser.get(userId));
        LOG.info("--------------------------");
        LOG.info("\n");
        return keyOfUser.get(userId);
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


        Integer nonc = new Integer(nonce);
        boolean isValidNonce = isValidRequestNonce(userId, nonc);
        LOG.info("Nonce - " + nonc.toString() + " is valid:" );
        LOG.info(isValidNonce);

        //generate session key for the user
        byte[] AESKeyBytes = generateAESSessionKey();

        //keep the session key in a hashmap
        setUserSessionKey(userId, AESKeyBytes);

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
            //LOG.info("Sending cskr ----------------");
            //LOG.info(cskr.toString());
            //LOG.info("-----------------------------------------------------");

            return cskr;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public LocationReport decipherLocationReport(Key sessionKey, CipheredLocationReport clr){

        try {

            //LOG.info("DECIPHER LOCATION REPORT ---------------------------------");«
            //LOG.info("session key :");
            //LOG.info(sessionKey);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sessionKey);

            byte[] cipheredLocationReportBytes = clr.getCipheredLocationReportBytes();
            //LOG.info("ciphered location report bytes - " + Base64.getEncoder().encodeToString(cipheredLocationReportBytes));
            byte[] locationReportBytes = cipher.doFinal(cipheredLocationReportBytes);

            //LOG.info("deciphered location report bytes - " + Base64.getEncoder().encodeToString(locationReportBytes));
            //LOG.info("DECIPHER LOCATION REPORT END ---------------------------------");
            return LocationReport.fromBytes(locationReportBytes);

        }

        catch(Exception e){
            LOG.info("Error decrypting ciphered location report");
            e.printStackTrace();

        }
        return null;
    }

    public boolean isValidRequestNonce(String userId, Integer nonce) {

        Boolean b = new Boolean(true);

        HashMap<Integer, Boolean> usedNoncesMap = usedNoncesOfUser.get(userId);

        if ( usedNoncesMap == null ){
            usedNoncesOfUser.put(userId, new HashMap<Integer, Boolean>());
            usedNoncesOfUser.get(userId).put(nonce, b);

            return true;
        }

        if( !usedNoncesMap.containsKey(nonce) ){
            usedNoncesMap.put(nonce, b);
            return true;
        }

        return false;
    }
}