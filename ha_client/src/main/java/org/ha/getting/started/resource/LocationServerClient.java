package org.ha.getting.started.resource;

import org.ha.getting.started.model.ObtainUserAtLocationRequest;
import org.ha.getting.started.model.ObtainLocationRequest;
import org.ha.getting.started.model.HaResponse;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/location")
@RegisterRestClient
public interface LocationServerClient {

    @POST
    @Path("/usersatlocation")
    HaResponse obtainUsersAtLocation(ObtainUserAtLocationRequest request);

    @POST
    @Path("/haobtain")
    HaResponse obtainLocation(ObtainLocationRequest request);
}
