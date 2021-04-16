package org.acme.getting.started;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.net.URI;
import java.net.URISyntaxException;



@Path("/location")
public class LocationResource {

    @ConfigProperty(name = "location.server.url")
    String l_srv_url;

    public LocationResource(){
    }

    @GET
    @Path("/{epoch}")
    public String obtain_location(@PathParam("epoch") int epoch) throws URISyntaxException {
        LocationServerClient lsc = RestClientBuilder.newBuilder()
                .baseUri(new URI(l_srv_url))
                .build(LocationServerClient.class);
        //@TODO -> Sign request
        LocationRequest lr = new LocationRequest(System.getenv("USERNAME"), epoch, "signature");
        String s = lsc.obtainLocationReport(lr);
        return s;
    }

}
