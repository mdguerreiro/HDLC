package org.acme.getting.started.resource;

import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
@RegisterRestClient
public interface WriteRegisterClient {

    @POST
    @Path("/")
    WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest writeRegiterRequest);
}
