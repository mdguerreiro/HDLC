package org.acme.getting.started.resource;

import org.acme.getting.started.ProofService;
import org.acme.getting.started.model.LocationProofReply;
import org.acme.getting.started.model.LocationProofRequest;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;


@Path("/proof")
public class ProofResource {

    public ProofResource(){
    }

    @Inject
    ProofService service;

    @POST
    @Path("/request")
    @Retry(maxRetries = 4)
    public LocationProofReply proof_request(LocationProofRequest lpr) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        return service.location_proof_request(lpr);
    }

}
