package org.acme.getting.started;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.w3c.dom.ls.LSOutput;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.*;


@Path("/proof")
public class ProofResource {

    public ProofResource(){
    }

    @Inject
    ProofService service;

    @Inject
    @RestClient
    ProofResourceClient proofResourceClient;


    @POST
    @Path("/request")
    public Response proof_request(LocationProofRequest lpr) {
        return service.location_proof_request(lpr);
    }

    @GET
    @Path("/test")
    public Response test(){
        LocationProofRequest lpr = new LocationProofRequest("user2", 0, 1);
        try{
            Response r = proofResourceClient.proof_request(lpr);
            System.out.println("EEEEEEEEH!!!!!!");
            System.out.println(r.getStatus());
            return r;
        }catch(org.jboss.resteasy.client.exception.ResteasyWebApplicationException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
