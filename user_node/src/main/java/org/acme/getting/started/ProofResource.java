package org.acme.getting.started;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.ls.LSOutput;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.FileReader;


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
    public LocationProofReply proof_request(LocationProofRequest lpr) {
        return service.location_proof_request(lpr);
    }

    @GET
    @Path("/test")
    public LocationProofReply test(){
        LocationProofRequest lpr = new LocationProofRequest("user2", 0, 1);
        try{
            LocationProofReply r = proofResourceClient.proof_request(lpr);
            System.out.println(r.status);
            return r;
        }catch(org.jboss.resteasy.client.exception.ResteasyWebApplicationException e){
            System.out.println("ERROR - Location was not valid");

        }
        return new LocationProofReply("DENIED");

    }

}
