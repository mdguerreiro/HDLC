package org.acme.getting.started;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserResource {

    @Inject
    UserService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create/{name}")
    public String create_user(@PathParam String name) {
        return service.create_user(name);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/get/{name}")
    public String get_user(@PathParam String name) {
        return service.get_user(name);
    }
}
