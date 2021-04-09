package org.acme.getting.started;

import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;


@RequestScoped
public class ProofService {
    private static final Logger LOG = Logger.getLogger(ProofService.class);

    public ProofService(){
    }

    @Inject
    EpochService es;

    public LocationProofReply location_proof_request(LocationProofRequest lpr) {
        int epoch = es.get_epoch();
        String my_username = System.getenv("USERNAME");
        LOG.info(String.format("LPR received from %s at epoch %d", lpr.username, epoch));
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(epoch).get(my_username);
        Location l = new Location(lpr.xLoc, lpr.yLoc);
        LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", my_username, my_Loc.get_X(), my_Loc.get_Y()));
        LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", lpr.username, l.get_X(), l.get_Y()));
        if (!AppLifecycleBean.is_Close(my_Loc, l)){
            LOG.error("LPR Received from " + lpr.username + " has invalid location.");
            return new LocationProofReply("DENIED", my_username);
        }
        LOG.info("Location confirmed. Sending LP Reply to " + lpr.username);
        return new LocationProofReply("APPROVED", my_username);
    }


}
