package org.acme.getting.started;

import org.acme.getting.started.model.WriteRegiterRequest;
import org.jboss.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class WriteRegisterService {
    private static final Logger LOG = Logger.getLogger(WriteRegisterService.class);

    public WriteRegisterService() {

    }

    public String submitWriteRegisterRequest(WriteRegiterRequest wrq) {
        String OK = "OK";
        LOG.info("Submitting Write Register Request " + OK);
        return OK;
    }
}
