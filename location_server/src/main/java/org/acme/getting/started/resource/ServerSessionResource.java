package org.acme.getting.started.resource;

import javax.inject.Inject;
import javax.ws.rs.*;

import org.acme.crypto.ServerSessionService;
import org.acme.getting.started.model.CipheredSessionKeyResponse;
import org.acme.getting.started.model.SignedSessionKeyRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

import java.security.*;


@Path("/session")
public class ServerSessionResource {

    @Inject
    ServerSessionService service;

    @POST
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public CipheredSessionKeyResponse submitSignedSessionKeyRequest(SignedSessionKeyRequest sskr) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException, SignatureException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, UnrecoverableKeyException,BadPaddingException {
        return service.handleSignedSessionKeyRequest(sskr);
    }

}
