package org.acme.getting.started;

import org.acme.lifecycle.AppLifecycleBean;
import org.jboss.logging.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.sound.midi.Soundbank;

@ApplicationScoped
public class EpochService {
    private static final Logger LOG = Logger.getLogger(EpochService.class);
    private int epoch = -1;
    public EpochService(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                epoch++;
                request_location_proof();
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, 10000);
    }

    private void request_location_proof(){
        String my_username = System.getenv("USERNAME");
        Location my_Loc = (Location) AppLifecycleBean.epochs.get(get_epoch()).get(my_username);
        Iterator it = AppLifecycleBean.epochs.get(get_epoch()).entrySet().iterator();
        while(it.hasNext()){
            Map.Entry elem = (Map.Entry)it.next();
            Location l = (Location) elem.getValue();
            if(!elem.getKey().equals(my_username)){
                if(AppLifecycleBean.is_Close(my_Loc, l)){
                    String hostname = AppLifecycleBean.hosts.get(elem.getKey());
                    // SEND A MESSAGE WITH LOCATION PROOF REQUEST
                    // STORE THE REPLY IN A REPLIES VECTOR
                    // SEND A MESSAGE TO THE SERVER WITH THE REPLIES VECTOR


                }
            }
        }
        //Location lpr_loc = new Location(lpr.xLoc, lpr.yLoc);

    }

    public int get_epoch() {
        LOG.info("Returning epoch time from service: " + epoch);
        return epoch;
    }

}
