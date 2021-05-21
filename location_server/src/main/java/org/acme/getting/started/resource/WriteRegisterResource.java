package org.acme.getting.started.resource;

import org.acme.getting.started.LocationService;
import org.acme.getting.started.WriteRegisterService;
import org.acme.getting.started.model.ValueRegisterReply;
import org.acme.getting.started.model.ValueRegisterRequest;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

@Path("/register")
public class WriteRegisterResource {

    @Inject
    WriteRegisterService writeRegisterService;



    @POST
    @Path("/write")
    public WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest wrq) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, URISyntaxException, InvalidKeyException {
        return writeRegisterService.submitWriteRegisterRequest(wrq);
    }

    @POST
    @Path("/value")
    public ValueRegisterReply submitValueRegisterRequest(ValueRegisterRequest vrq) {
        return writeRegisterService.submitValueRegisterRequest(vrq);
    }
}

