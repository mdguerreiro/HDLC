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
        LOG.info("LPR Received from " + lpr.username);
        System.out.println(es.get_epoch());
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(es.get_epoch()).get(System.getenv("USERNAME"));
        Location lpr_loc = new Location(lpr.xLoc, lpr.yLoc);
        System.out.println("MY LOCATION AT TIME : " + System.getenv("USERNAME") + " " + es.get_epoch() + " " + my_Loc.get_X() + " " + my_Loc.get_Y());
        System.out.println("Remote user AT TIME : " + lpr.username + " " + es.get_epoch() + " " + lpr_loc.get_X() + " " + lpr_loc.get_Y());
        if (!AppLifecycleBean.is_Close(my_Loc, lpr_loc)){
            LOG.error("LPR Received from " + lpr.username + " has invalid location.");
            return new LocationProofReply("DENIED");
        }
        LOG.info("Location confirmed. Sending LP Reply to " + lpr.username);
        return new LocationProofReply("APPROVED");
    }


}
