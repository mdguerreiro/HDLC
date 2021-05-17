package org.acme.getting.started.resource;

import org.acme.getting.started.model.WriteRegiterRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
@RegisterRestClient
public interface WriteRegisterClient {

    @POST
    @Path("/")
    String submitWriteRegisterRequest(WriteRegiterRequest writeRegiterRequest);
}
