package org.acme.getting.started;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/session")
@RegisterRestClient
public interface SessionServerClient {

    @POST
    @Path("/")
    CipheredSessionKeyResponse submitSignedSessionKeyRequest(SignedSessionKeyRequest sskr);
}
