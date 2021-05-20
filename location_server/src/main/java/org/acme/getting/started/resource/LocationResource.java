package org.acme.getting.started.resource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.acme.crypto.ServerSessionService;
import org.acme.getting.started.model.CipheredLocationReport;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.LocationService;
import org.acme.getting.started.model.LocationRequest;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

@Path("/location")
public class LocationResource {

    @Inject
    LocationService service;

    @Inject
    ServerSessionService sessionService;

    @POST
    @Path("/")
    public String submitCipheredLocationReport(CipheredLocationReport clr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, URISyntaxException, UnrecoverableKeyException {

        String userId = clr.getUsername();

        Key userSessionKey = sessionService.getUserSessionKey(userId);
        LocationReport lr = sessionService.decipherLocationReport(userSessionKey, clr);

        return service.submit_location_report(lr);

    }

    @POST
    @Path("/obtain")
    public String obtainLocationReport(LocationRequest lr) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        // @TODO: LOG INFO
        System.out.println("RECEIVED LOCATION REQUEST");
        return service.get_location_report(lr.username, lr.epoch, lr.signatureBase64);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{x}/{y}/{epoch}")
    public String obtainUsersAtLocation(@PathParam("x") String x, @PathParam("y") String y, @PathParam("epoch") String epoch) throws URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        // @TODO: LOG INFO
        return service.get_user_at(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(epoch));
    }
}
