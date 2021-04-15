package org.acme.getting.started;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.Key;

import java.util.Base64;

@Path("/location")
public class LocationResource {

    @Inject
    LocationService service;

    @Inject
    ServerSessionService sessionService;

    /*
    @POST
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String submitLocationReport(LocationReport lr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        return service.submit_location_report(lr);
    }

    */

    @POST
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String submitCipheredLocationReport(CipheredLocationReport clr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {

        String userId = clr.getUsername();

        Key userSessionKey = sessionService.getUserSessionKey(userId);
        LocationReport lr = sessionService.decipherLocationReport(userSessionKey, clr);

        return service.submit_location_report(lr);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{userID}/{epoch}")
    public String obtainLocationReport(@PathParam("userID") String userID, @PathParam("epoch") String epoch) {
        // @TODO: LOG INFO
        return service.get_location_report(userID, Integer.parseInt(epoch));
    }
}
