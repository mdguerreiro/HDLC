package org.acme.getting.started;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/proof")
@RegisterRestClient
public interface ProofResourceClient {

    @POST
    @Path("/request")
    LocationProofReply proof_request(LocationProofRequest lpr);
}
