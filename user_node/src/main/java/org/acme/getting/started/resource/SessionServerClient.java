package org.acme.getting.started.resource;

import org.acme.getting.started.model.CipheredSessionKeyResponse;
import org.acme.getting.started.model.SignedSessionKeyRequest;
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
