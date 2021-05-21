package org.acme.getting.started;

import org.acme.crypto.SignatureService;
import org.acme.getting.started.model.*;
import org.acme.getting.started.resource.WriteRegisterClient;
import org.acme.getting.started.storage.LocationReportsStorage;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Map;

@Singleton
public class WriteRegisterService {
    private static final Logger LOG = Logger.getLogger(WriteRegisterService.class);

    @Inject
    SignatureService signatureService;

    @Inject
    LocationService locationService;

    public WriteRegisterService() {

    }

    public WriteRegisterReply replyWriteRegisterWithSignature(WriteRegisterReply writeRegisterReply) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String myServerName = System.getenv("SERVER_NAME");

        String signatureBase64 = signatureService.generateSha256WithRSASignatureForWriteReply(myServerName, writeRegisterReply.acknowledgment, writeRegisterReply.ts);
        writeRegisterReply.signatureBase64 = signatureBase64;
        return writeRegisterReply;
    }

    public WriteRegisterReply submitWriteRegisterRequest(WriteRegisterRequest writeRegisterRequest) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException, InvalidKeyException, UnrecoverableKeyException, URISyntaxException {

        if(writeRegisterRequest.wts > locationService.data_ts){
            locationService.data_ts = writeRegisterRequest.wts;
            locationService.validateLocationReport(writeRegisterRequest.locationReport);
        }

        LOG.info("Submitting Write Register Request ");
        // FOR LOOP FOR ALL PROCESSES LISTENING
        Iterator it = AppLifecycleBean.listening.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String url = AppLifecycleBean.location_servers.get((String) pair.getKey());
            if((Integer) pair.getValue() != -1){
                WriteRegisterClient writeRegisterClient = RestClientBuilder.newBuilder()
                        .baseUri(new URI(url))
                        .build(WriteRegisterClient.class);
                ValueRegisterRequest valueRegisterRequest = new ValueRegisterRequest(writeRegisterRequest.locationReport,
                        writeRegisterRequest.wts, (Integer) pair.getValue());
                ValueRegisterReply valueRegisterReply = writeRegisterClient.submitValueRegisterRequest(valueRegisterRequest);
            }

        }

        WriteRegisterReply writeRegisterReply = new WriteRegisterReply("true", writeRegisterRequest.wts);

        return replyWriteRegisterWithSignature(writeRegisterReply);
    }

    public ValueRegisterReply submitValueRegisterRequest(ValueRegisterRequest vrq) {
        return new ValueRegisterReply();
    }
}
