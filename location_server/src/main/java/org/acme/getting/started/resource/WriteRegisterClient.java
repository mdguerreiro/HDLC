package org.acme.getting.started.resource;

import org.acme.getting.started.model.ValueRegisterReply;
import org.acme.getting.started.model.ValueRegisterRequest;
import org.acme.getting.started.model.WriteRegisterReply;
import org.acme.getting.started.model.WriteRegisterRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
@RegisterRestClient
public interface WriteRegisterClient {

    @POST
    @Path("/write")
    WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest writeRegisterRequest);

    @POST
    @Path("/value")
    ValueRegisterReply submitValueRegisterRequest(ValueRegisterRequest valueRegisterRequest);
}
