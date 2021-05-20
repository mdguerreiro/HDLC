package org.acme.getting.started.resource;

import org.acme.getting.started.ReadRegisterService;
import org.acme.getting.started.WriteRegisterService;
import org.acme.getting.started.model.ReadRegisterReply;
import org.acme.getting.started.model.ReadRegisterRequest;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Path("/register/read")
public class ReadRegisterResource {

    @Inject
    ReadRegisterService readRegisterService;

    @POST
    @Path("/")
    public ReadRegisterReply submitReadRegisterRequest(ReadRegisterRequest rrq) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        return readRegisterService.submitReadRegisterRequest(rrq);
    }
}

