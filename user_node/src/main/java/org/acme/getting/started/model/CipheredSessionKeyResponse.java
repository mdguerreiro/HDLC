package org.acme.getting.started.model;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;


public class CipheredSessionKeyResponse implements Serializable {

    //AES key ciphered with user public key
    private byte[] cipheredAESKeyBytes;

    //Hash of AES key signed with server private key
    private byte[] serverSignature;

    public CipheredSessionKeyResponse(){

    }

    public CipheredSessionKeyResponse(byte[] cipheredAESKeyBytes, byte[] serverSignature){
        this.cipheredAESKeyBytes = cipheredAESKeyBytes;
        this.serverSignature = serverSignature;
    }

    public byte[] getCipheredAESKeyBytes(){
        return cipheredAESKeyBytes;
    }

    public byte[] getServerSignature(){
        return serverSignature;
    }


    public String toString(){
        return "cipheredAESKey - {" + new String(Base64.encodeBase64(cipheredAESKeyBytes)) + "}\n" + "serverSignature - {" + new String(Base64.encodeBase64(serverSignature)) + ")\n";

    }
}