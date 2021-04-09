package org.acme.getting.started;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/location")
public class LocationResource {

    @Inject
    LocationService service;

    @POST
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String submitLocationReport(LocationReport lr) {
        return service.submit_location_report(lr);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{userID}/{epoch}")
    public String obtainLocationReport(@PathParam("userID") String userID, @PathParam("epoch") String epoch) {
        System.out.println("RECEIVED REQUEST");
        System.out.println(userID);
        System.out.println(epoch);
        return service.get_location_report(userID, Integer.parseInt(epoch));
    }
}
