package org.acme.crypto;

import org.acme.crypto.SignatureService;
import org.jboss.logging.Logger;
import javax.inject.Singleton;

import io.quarkus.runtime.Startup;


import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


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


}