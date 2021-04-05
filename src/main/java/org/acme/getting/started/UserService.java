package org.acme.getting.started;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);
    private final ConcurrentHashMap<String, User> users;

    public UserService(){
        this.users = new ConcurrentHashMap<>();
    }
    
    public String create_user(String name) {
        User u = new User(name);
        this.users.put(name, u);
        LOG.info("User: " + name + "added to system.");
        return "hello " + name;
    }

    public String get_user(String name) {

        User u = this.users.get(name);
        if(u != null){
            LOG.info("User: " + u.get_name() + " logged to system.");
            return "hello " + u.get_name();
        }
        LOG.info("User: " + name + " not found.");
        return "not found";
    }

}
