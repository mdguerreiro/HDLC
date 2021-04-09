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

    @POST
    @Path("/request")
    public LocationProofReply proof_request(LocationProofRequest lpr) {
        return service.location_proof_request(lpr);
    }

}
