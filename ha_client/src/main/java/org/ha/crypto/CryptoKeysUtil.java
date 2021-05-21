package org.ha.crypto;

import org.ha.utils.Util;
import org.jboss.logging.Logger;

import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

public class CryptoKeysUtil {
    private static final Logger LOG = Logger.getLogger(CryptoKeysUtil.class);

    static public byte[] generateAESSessionKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        System.out.println("Finish generating AES key");
        byte[] encoded = key.getEncoded();

        LOG.info("Plain Session Key generated  - " + Base64.getEncoder().encodeToString(key.getEncoded()) );

        return encoded;

    }

    public static PublicKey getPublicKeyFromKeystore(String serverName) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        String keyAlias = serverName + "_key_store";
        String keyStoreLocation = "keys/" + serverName + "_key_store.p12";
        String keyStorePassword = serverName + "_pwd";

        InputStream keyPairAsStream = Util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());

        Certificate certificate = keyStore.getCertificate(keyAlias);

        return certificate.getPublicKey();
    }


    static public PrivateKey getPrivateKeyFromKeystore(String serverName) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String keyAlias = serverName + "_key_store";
        String keyStoreLocation = "keys/" + serverName + "_key_store.p12";
        String keyStorePassword = serverName + "_pwd";

        InputStream keyPairAsStream = Util.getFileFromResourceAsStream(keyStoreLocation);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyPairAsStream, keyStorePassword.toCharArray());
        return (PrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
    }
}
