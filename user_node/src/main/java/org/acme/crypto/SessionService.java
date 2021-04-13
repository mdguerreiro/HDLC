package org.acme.crypto;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;


import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.io.IOException;


import org.acme.getting.started.SessionKeyRequest;
import org.acme.getting.started.SignedSessionKeyRequest;


@Startup
@Singleton
public class SessionService {

    private static final Logger LOG = Logger.getLogger(SignatureService.class);


    public byte[] generateAESSessionKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        System.out.println("Finish generating AES key");
        byte[] encoded = key.getEncoded();

        return encoded;


    }


    public SignedSessionKeyRequest signSessionKeyRequest( SessionKeyRequest skr, PrivateKey privKey ) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        byte[] skrBytes = SignatureService.serialize(skr);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privKey);

        signature.update(skrBytes);


        return new SignedSessionKeyRequest(skr, signature.sign());

    }


}