package org.ha.getting.started.resource;

import org.ha.getting.started.model.ObtainUserAtLocationRequest;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/location")
@RegisterRestClient
public interface LocationServerClient {

    @POST
    @Path("/usersatlocation")
    String obtainUsersAtLocation(ObtainUserAtLocationRequest request);

}
