package org.acme.lifecycle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.acme.getting.started.model.Location;
import org.jboss.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = Logger.getLogger("ListenerBean");
    // Shared Data Structures
    public static HashMap<String, String> hosts = new HashMap<>();
    public static ArrayList<HashMap> epochs = new ArrayList<>();


    public static final String USERS_PATH = "classes/json/users.json";
    public static final String GRID_PATH = "classes/json/grid.json";

    void onStart(@Observes StartupEvent ev) throws InterruptedException {
        Thread.sleep(10000);
        LOGGER.info("The application is starting...");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        LOGGER.info("Loading users/hosts info file...");
        load_json_users();
        load_json_grid();
        LOGGER.info("Loading bluetooth grid emulator...");
        LOGGER.info("Loading epoch time clock...");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

    void load_json_users(){
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(USERS_PATH));

            for (Object temp : jsonObject.keySet()){
                hosts.put((String) temp, (String) jsonObject.get(temp));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    void load_json_grid(){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject a = (JSONObject) jsonParser.parse(new FileReader(GRID_PATH));
            for (Object o : a.keySet())
            {
                HashMap<String, Location> users_loc = new HashMap<String, Location>();
                JSONObject epoch = (JSONObject) a.get(o);
                for(Object user : epoch.keySet()){
                    JSONObject coords = (JSONObject) epoch.get(user);
                    int x = Integer.parseInt((String) coords.get("x"));
                    int y = Integer.parseInt((String) coords.get("y"));
                    users_loc.put((String)user, new Location(x, y));
                }
                epochs.add(users_loc);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean is_Close(Location l1, Location l2){
        //@TODO -> Improve distance function
        return (Math.abs(l2.get_X()-l1.get_X()) <= 3 && Math.abs(l2.get_Y()-l1.get_Y()) <= 3);
    }
}