package org.acme.getting.started;



public class CipheredSessionKeyResponse {


    //AES key ciphered with user public key
    private byte[] cipheredAESKeyBytes;

    //Hash of AES key signed with server private key
    private byte[] serverSignature;

    public byte[] getCipheredAESKeyBytes(){
        return cipheredAESKeyBytes;
    }

    public byte[] getServerSignature(){
        return serverSignature;
    }


}