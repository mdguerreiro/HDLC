package org.acme.crypto;

import org.acme.getting.started.model.*;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;

import java.security.cert.CertificateException;

import java.security.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

@Startup
@Singleton
public class SessionService {

    private static final Logger LOG = Logger.getLogger(SessionService.class);

    Key sessionKey = null;

    public Key getSessionKey(){
        return sessionKey;
    }

    public void setSessionKey(Key key){
        this.sessionKey = key;
    }

    public SignedSessionKeyRequest signSessionKeyRequest(SessionKeyRequest skr, PrivateKey privKey ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privKey);

        signature.update(skr.getUserId().getBytes(StandardCharsets.UTF_8));
        signature.update(String.valueOf(skr.getNonce()).getBytes(StandardCharsets.UTF_8));

        return new SignedSessionKeyRequest(skr, signature.sign());
    }

    public boolean verifyCipheredSessionKeyResponse( CipheredSessionKeyResponse cskr) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException{

        byte[] cipheredKeyBytes = cskr.getCipheredAESKeyBytes();
        byte[] serverSignature = cskr.getServerSignature();

        LOG.info("veryfiying cskr ----------------");
        LOG.info(cskr.toString());
        LOG.info("-----------------------------------------------------");

        PublicKey serverPub = CryptoKeysUtil.getPublicKeyFromKeystore("location_server");

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
            PublicKey serverPub = CryptoKeysUtil.getPublicKeyFromKeystore("location_server");
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

            PrivateKey userPriv = CryptoKeysUtil.getPrivateKeyFromKeystore(System.getenv("USERNAME"));
            Key key = decipherSessionKey( cskr.getCipheredAESKeyBytes(), userPriv );

            LOG.info("Session Key deciphered - " + Base64.getEncoder().encodeToString(key.getEncoded()) );
            this.sessionKey = key;
            return key;

        }
        catch(Exception e){
            LOG.info("Error handling cskr");
            e.printStackTrace();
        }

       return null;
    }

    public CipheredLocationReport cipherLocationReport(Key sessionKey, LocationReport lr){
        try {
            byte[] locationReportBytes = LocationReport.toBytes(lr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey);

            byte[] cipheredLocationReportBytes = cipher.doFinal(locationReportBytes);

            return new CipheredLocationReport(lr.username, cipheredLocationReportBytes);
        }
        catch(Exception e){
            e.printStackTrace();

            return new CipheredLocationReport("error", (new String("error").getBytes() ) );
        }
    }

    public LocationReport decipherLocationReport(Key sessionKey, CipheredLocationReport clr){

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
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