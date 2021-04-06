package org.acme.getting.started;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/epoch")
public class EpochResource {

    @Inject
    EpochService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String get_epoch(@PathParam String name) {
        return service.get_epoch();
    }

}
