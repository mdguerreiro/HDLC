package org.acme.getting.started;

import io.quarkus.runtime.Startup;
import org.acme.lifecycle.AppLifecycleBean;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.inject.Singleton;


@Startup
@Singleton
public class EpochService {
    private static final Logger LOG = Logger.getLogger(EpochService.class);
    private int epoch = -1;

    public EpochService(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                epoch++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    request_location_proof();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, 60000);

    }

    private void request_location_proof() throws URISyntaxException {
        String my_username = System.getenv("USERNAME");
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(get_epoch()).get(my_username);
        Iterator it = AppLifecycleBean.epochs.get(get_epoch()).entrySet().iterator();
        LocationProofRequest lpr = new LocationProofRequest(my_username, my_Loc.get_X(), my_Loc.get_Y());
        List<LocationProofReply> replies = new ArrayList<>();
        while(it.hasNext()){
            Map.Entry elem = (Map.Entry)it.next();
            Location l = (Location) elem.getValue();
            if(!elem.getKey().equals(my_username)){
                if(AppLifecycleBean.is_Close(my_Loc, l)){
                    LOG.info(String.format("Process %s found %s close at epoch %d. Starting broadcast.", my_username,
                            elem.getKey(), get_epoch()));
                    LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", my_username, my_Loc.get_X(),
                            my_Loc.get_Y()));
                    LOG.info(String.format("%s process coordinates -> X = %d, Y= %d", elem.getKey(), l.get_X(),
                            l.get_Y()));
                    String hostname = AppLifecycleBean.hosts.get(elem.getKey());
                    ProofResourceClient prc = RestClientBuilder.newBuilder()
                            .baseUri(new URI(hostname))
                            .build(ProofResourceClient.class);
                    LocationProofReply lp_reply = prc.proof_request(lpr);
                    replies.add(lp_reply);
                    // @TODO Send to server
                }
            }
        }
    }

    public int get_epoch() {
        //LOG.info("Returning epoch time from service: " + epoch);
        return epoch;
    }

}
