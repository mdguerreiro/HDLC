package org.acme.getting.started;

import org.acme.lifecycle.AppLifecycleBean;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@ApplicationScoped
public class ProofService {
    private static final Logger LOG = Logger.getLogger(ProofService.class);

    public ProofService(){

    }
    
    public Response location_proof_request(LocationProofRequest lpr) {
        LOG.info("LPR Received from " + lpr.username);
        System.out.println(AppLifecycleBean.hosts.get(lpr.username));
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(0).get(System.getenv("USERNAME"));
        Location lpr_loc = new Location(lpr.xLoc, lpr.yLoc);

        if (is_Close(my_Loc, lpr_loc)){
            System.out.println("IS CLOSE");

            return Response.ok(new LocationProofReply()).build();
        }
        System.out.println("AINT CLOSE");

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private boolean is_Close(Location l1, Location l2){
        return (Math.abs(l2.get_X()-l1.get_X()) <= 1 && Math.abs(l2.get_Y()-l1.get_Y()) <= 1);
    }


}
