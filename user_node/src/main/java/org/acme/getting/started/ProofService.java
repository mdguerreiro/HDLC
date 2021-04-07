package org.acme.getting.started;

import org.acme.lifecycle.AppLifecycleBean;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;


@RequestScoped
public class ProofService {
    private static final Logger LOG = Logger.getLogger(ProofService.class);

    public ProofService(){
    }


    public LocationProofReply location_proof_request(LocationProofRequest lpr) {
        LOG.info("LPR Received from " + lpr.username);
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(0).get(System.getenv("USERNAME"));
        Location lpr_loc = new Location(lpr.xLoc, lpr.yLoc);

        if (!AppLifecycleBean.is_Close(my_Loc, lpr_loc)){
            LOG.error("LPR Received from " + lpr.username + " has invalid location.");
            return new LocationProofReply("DENIED");
        }
        LOG.info("Location confirmed. Sending LP Reply to " + lpr.username);
        return new LocationProofReply("APPROVED");
    }


}
