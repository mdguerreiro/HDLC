package org.acme.getting.started;

import org.jboss.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EpochService {
    private static final Logger LOG = Logger.getLogger(EpochService.class);
   // private final ConcurrentHashMap<User, int[]> users;
    private int epoch;
    public EpochService(){
        epoch = 0;
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LOG.info("Clock tick: epoch updated");
                epoch++;
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, 60000);
    }



    public String get_epoch() {
        LOG.info("Returning epoch time from service");
        return Integer.toString(epoch);
    }

}
