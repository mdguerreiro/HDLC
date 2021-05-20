package org.acme.getting.started;

import org.acme.getting.started.model.*;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ReadRegisterService {
    private static final Logger LOG = Logger.getLogger(ReadRegisterService.class);

    @Inject
    LocationService locationService;


    public ReadRegisterService() {

    }

    public ReadRegisterReply submitReadRegisterRequest(ReadRegisterRequest readRegisterRequest) {
        String myServerName = System.getenv("SERVER_NAME");
        int epoch = readRegisterRequest.epoch;
        String username = readRegisterRequest.username;

        LocationReport lr;
        try {
            lr = locationService.users.get(username).get(epoch);
        } catch (NullPointerException e) {
            lr = null;
        }
        ReadRegisterReply readRegisterReply = new ReadRegisterReply();
        readRegisterReply.signatureBase64 = "TEST";
        readRegisterReply.senderServerName = myServerName;
        readRegisterReply.ts = locationService.data_ts;
        readRegisterReply.rid = readRegisterRequest.rid;
        readRegisterReply.lr = lr;
        LOG.info("READ REQUEST MADE - SENDING REPLY TO READER SERVER");
        return readRegisterReply;
    }
}
