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

@Path("/session")
public class ServerSessionResource {

    @Inject
    ServerSessionService service;

    @POST
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public CipheredSessionKeyResponse submitSignedSessionKeyRequest(SignedSessionKeyRequest sskr) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException {
        return service.handleSignedSessionKeyRequest(sskr);
    }


}
