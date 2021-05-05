package org.acme.crypto;

import org.acme.getting.started.model.CipheredLocationReport;
import org.acme.getting.started.model.CipheredSessionKeyResponse;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.model.SignedSessionKeyRequest;
import org.jboss.logging.Logger;
import javax.crypto.spec.SecretKeySpec;


import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import java.security.*;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;

import java.nio.charset.StandardCharsets;


import java.util.HashMap;

@ApplicationScoped
public class ServerSessionService {

    private static final Logger LOG = Logger.getLogger(SignatureService.class);
    private static HashMap<String,Key> keyOfUser = new HashMap<String,Key>();
    private static HashMap<String,HashMap> usedNoncesOfUser = new HashMap<String,HashMap>();


    public static void setUserSessionKey(String userId, byte[] sessionKeyBytes){
        Key sessionKey = new SecretKeySpec(sessionKeyBytes, 0, sessionKeyBytes.length, "AES");
        keyOfUser.put(userId, sessionKey);

        LOG.info("setting session key of user - " + userId);
        LOG.info("session key of user - " + sessionKey );
        //LOG.info("session key in the hashmap is " + getUserSessionKey(userId));

    }

    public static Key getUserSessionKey(String userId){
        LOG.info("----------getusersessionkey()----------------");
        LOG.info("getting key in the hashmap for user - " + userId);
        LOG.info("session key in the hashmap is " + ServerSessionService.keyOfUser.get(userId));
        LOG.info("--------------------------");
        return keyOfUser.get(userId);
    }

    public CipheredSessionKeyResponse handleSignedSessionKeyRequest(SignedSessionKeyRequest sskr ) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, UnrecoverableKeyException, BadPaddingException{

        //LOG.info("received session key request ------------------------");
        //LOG.info(sskr.toString());
        //LOG.info("ssk END -------------------------");

        String userId = sskr.getSessionKeyRequest().getUserId();
        int nonce =  sskr.getSessionKeyRequest().getNonce();

        PublicKey userPub = CryptoKeysUtil.getPublicKeyFromKeystore(userId);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(userPub);

        signature.update(userId.getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(nonce).getBytes(StandardCharsets.UTF_8));

        boolean validSignature = signature.verify( sskr.getSignature() );

        if(validSignature) {
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
        byte[] AESKeyBytes = CryptoKeysUtil.generateAESSessionKey();

        //keep the session key in a hashmap
        setUserSessionKey(userId, AESKeyBytes);

        //cipher AES session key with the public key of the user
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, userPub);

        byte[] cipheredAESKeyBytes = cipher.doFinal(AESKeyBytes);

        //sign the ciphered session key with the private key of the server
        PrivateKey serverPriv;
        try {
             serverPriv = CryptoKeysUtil.getPrivateKeyFromKeystore("location_server");
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