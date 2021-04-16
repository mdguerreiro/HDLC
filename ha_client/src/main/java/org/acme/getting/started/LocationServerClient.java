package org.acme.getting.started;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/location")
@RegisterRestClient
public interface LocationServerClient {

    @POST
    @Path("/")
    String submitLocationReport(LocationReport lr);
}
