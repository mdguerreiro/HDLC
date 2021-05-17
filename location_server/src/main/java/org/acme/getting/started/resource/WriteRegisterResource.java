package org.acme.getting.started.resource;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.WriteRegisterService;
import org.acme.getting.started.model.WriteRegiterRequest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class WriteRegisterResource {

    @Inject
    WriteRegisterService writeRegisterService;

    @POST
    @Path("/")
    public String submitWriteRegisterRequest(WriteRegiterRequest wrq) {
        return writeRegisterService.submitWriteRegisterRequest(wrq);
    }
}

