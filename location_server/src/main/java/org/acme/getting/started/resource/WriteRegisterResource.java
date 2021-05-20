package org.acme.getting.started.resource;

import org.acme.getting.started.LocationService;
import org.acme.getting.started.WriteRegisterService;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Path("/register")
public class WriteRegisterResource {

    @Inject
    WriteRegisterService writeRegisterService;

    @Inject
    LocationService locationService;

    @POST
    @Path("/")
    public WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest wrq) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException {
        locationService.validateLocationReport(wrq.locationReport);
        return writeRegisterService.submitWriteRegisterRequest(wrq);
    }
}

