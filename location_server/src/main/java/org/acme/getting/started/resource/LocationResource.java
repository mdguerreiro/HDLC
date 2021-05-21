package org.acme.getting.started.resource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.acme.crypto.ServerSessionService;
import org.acme.crypto.SignatureService;

import org.acme.getting.started.model.CipheredLocationReport;
import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.model.ha.ObtainUserAtLocationRequest;
import org.acme.getting.started.model.ha.ObtainLocationRequest;
import org.acme.getting.started.model.ha.HaResponse;

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
    public HaResponse obtainUsersAtLocation(ObtainUserAtLocationRequest request)   {
        // @TODO: LOG INFO
        HaResponse response;
        try{
            boolean isValidSignature = SignatureService.verifySignature(request);
            if(!isValidSignature){
                response = new HaResponse("Invalid Signature", request.getNonce()+1 ,"invalid signature" );
                String signature = SignatureService.signHaResponse(response);
                response.setSignature(signature);

                return response;

            }
        }
        catch(Exception e){
            response = new HaResponse("Error verifying", request.getNonce()+1, "error verifying");
            String signature = SignatureService.signHaResponse(response);
            response.setSignature(signature);
            return response;
        }

        if(!service.isValidLocationReportNonce(request.getHaId(), request.getNonce())){
            response = new HaResponse("invalid nonce", request.getNonce()+1, "invalid nonce");
            String signature = SignatureService.signHaResponse(response);
            response.setSignature(signature);
            return response;
        }
        service.addUserNonce(request.getHaId(), request.getNonce());


        String text = service.get_user_at(
                request.getX(),
                request.getY(),
                request.getEpoch()
        );

        response = new HaResponse(text, request.getNonce()+1 ,"signature" );
        String signature = SignatureService.signHaResponse(response);
        response.setSignature(signature);
        return new HaResponse(text,request.getNonce()+1, signature);

    }

    @POST
    @Path("/haobtain")
    public HaResponse obtainLocation(ObtainLocationRequest request)  {
        // @TODO: LOG INFO

        HaResponse response;
        try{
            boolean isValidSignature = SignatureService.verifySignature(request);
            if(!isValidSignature){
                response = new HaResponse("Invalid Signature", request.getNonce()+1 ,"invalid nonce" );
                String signature = SignatureService.signHaResponse(response);
                response.setSignature(signature);
                return response;

            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(request.getHaSignature());

            response = new HaResponse("Invalid Signature", request.getNonce()+1 ,"invalid signature" );
            String signature = SignatureService.signHaResponse(response);
            response.setSignature(signature);
            return response;

        }

        if(!service.isValidLocationReportNonce(request.getHaId(), request.getNonce())){
            response = new HaResponse("Invalid Signature", request.getNonce()+1 ,"invalid nonce" );
            String signature = SignatureService.signHaResponse(response);
            response.setSignature(signature);

            return response;
        }

        service.addUserNonce(request.getHaId(), request.getNonce());


        String text = service.get_location_report(request.getUserId(), request.getEpoch(), request.getHaSignature());

        response = new HaResponse(text, request.getNonce()+1 ,"" );
        String signature = SignatureService.signHaResponse(response);
        response.setSignature("hello world");


        response = new HaResponse(response.getText(), response.getNonce(), "hello world");

        return response;
    }
}
