package org.acme.getting.started.resource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.acme.crypto.ServerSessionService;
import org.acme.crypto.SignatureService;

import org.acme.getting.started.model.CipheredLocationReport;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.model.ha.ObtainUserAtLocationRequest;

import org.acme.getting.started.LocationService;
import org.acme.getting.started.model.LocationRequest;


import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.Key;

@Path("/location")
public class LocationResource {

    @Inject
    LocationService service;

    @Inject
    ServerSessionService sessionService;

    @POST
    @Path("/")
    public String submitCipheredLocationReport(CipheredLocationReport clr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {

        String userId = clr.getUsername();

        Key userSessionKey = sessionService.getUserSessionKey(userId);
        LocationReport lr = sessionService.decipherLocationReport(userSessionKey, clr);

        return service.submit_location_report(lr);

    }

    @POST
    @Path("/obtain")
    public String obtainLocationReport(LocationRequest lr) {
        // @TODO: LOG INFO
        System.out.println("RECEIVED LOCATION REQUEST");
        return service.get_location_report(lr.username, lr.epoch, lr.signatureBase64);
    }

    @POST
    @Path("/usersatlocation")
    public String obtainUsersAtLocation(ObtainUserAtLocationRequest request) {
        // @TODO: LOG INFO

        try{
            boolean isValidSignature = SignatureService.verifySignature(request);
            if(!isValidSignature){
                return "Invalid Signature";
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(request.getHaSignature());
            return "Error verifying signature";
        }

        return service.get_user_at(
                request.getX(),
                request.getY(),
                request.getEpoch()
        );
    }
}
