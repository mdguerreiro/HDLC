package org.acme.getting.started.resource;

import org.acme.getting.started.model.ReadRegisterReply;
import org.acme.getting.started.model.ReadRegisterRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register/read")
@RegisterRestClient
public interface ReadRegisterClient {

    @POST
    @Path("/")
    ReadRegisterReply submitReadRegisterRequestToGetLocationReport(ReadRegisterRequest readRegisterRequest);

    @POST
    @Path("/get-at")
    ReadRegisterReply submitReadRegisterRequestToGetUsersAtPosition(ReadRegisterRequest readRegisterRequest);
}
