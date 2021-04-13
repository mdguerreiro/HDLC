package org.acme.crypto;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;

import java.io.InputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;


import java.security.*;


import java.io.IOException;

import org.acme.utils.Util;


import java.nio.charset.StandardCharsets;

import org.acme.getting.started.SessionKeyRequest;
import org.acme.getting.started.SignedSessionKeyRequest;
import org.acme.getting.started.CipheredSessionKeyResponse;


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



    public boolean verifyCipheredSessionKeyResponse( CipheredSessionKeyResponse cskr) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException{

        byte[] cipheredKeyBytes = cskr.getCipheredAESKeyBytes();
        byte[] serverSignature = cskr.getServerSignature();

        PublicKey serverPub = getPublicKeyFromKeystore("location_server");

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(serverPub);

        signature.update(cipheredKeyBytes);

        return signature.verify(serverSignature);
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

    public Key decipherSessionKeyResponse( CipheredSessionKeyResponse cskr, PrivateKey userKey){

        byte[] cipheredAESKeyBytes = cskr.getCipheredAESKeyBytes();

        return null;
    }


    public CipheredSessionKeyResponse handleSignedSessionKeyRequest( SignedSessionKeyRequest sskr ) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,SignatureException, InvalidKeyException{

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

        //TODO
        return null;
    }

}