package org.acme.getting.started;

import org.jboss.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LocationService {
    private static final Logger LOG = Logger.getLogger(LocationService.class);
   // private final ConcurrentHashMap<User, int[]> users;

    public LocationService(){
        //this.users = new ConcurrentHashMap<>();
    }
    
    public String greeting(String name) {
        LOG.info("IN MEMORY: " + name);
        return "hello " + name;
    }

}
