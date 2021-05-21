package org.acme.crypto.pow;

import java.util.Base64;
import java.util.Random;

import java.security.MessageDigest;



class ProofOfWorkService {

    Random rand;

    public ProofOfWorkService(){
        rand = new Random();
    }


    public boolean isValidProofOfWork(byte[] hashArray){
        String hashString64 = Base64.getEncoder().encodeToString(hashArray);
        hashString64 = hashString64.toLowerCase();

        return hashString64.contains("sec");
    }


    public boolean verify(byte[] requestByteArray, byte[] solution ) throws Exception{

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(requestByteArray);
        digest.update(solution);

        byte[] hashByteArray = digest.digest();

        return isValidProofOfWork(hashByteArray);


    }


    public byte[] solve( byte[] requestByteArray ) throws Exception{

        int iterations = 0;
        boolean isSolved = false;
        byte[] solution = new byte[16];

        while (!isSolved) {


            rand.nextBytes(solution);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(requestByteArray);
            digest.update(solution);

            byte[] hashByteArray = digest.digest();

            isSolved = isValidProofOfWork(hashByteArray);
            iterations += 1;

        }

        return solution;
    }
}