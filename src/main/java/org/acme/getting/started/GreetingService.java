package org.acme.getting.started;

import org.jboss.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;

@ApplicationScoped
public class GreetingService {
    private static final Logger LOG = Logger.getLogger(GreetingService.class);

    String name;

    public GreetingService(){
        this.name = "Zé";
    }
    
    public String greeting(String name) {
        LOG.info("IN MEMORY: " + this.name);
        this.name = name;
        return "hello " + this.name;
    }

}
