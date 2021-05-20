package org.ha.lifecycle.pto;

import io.quarkus.runtime.Startup;

import org.ha.utils.Util;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
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



    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
        //System.out.println("Working Directory = " + System.getProperty("user.dir"));
        //LOGGER.info("Loading users/hosts info file...");
        //load_json_users();
        //load_json_grid();
        //LOGGER.info("Loading bluetooth grid emulator...");
        //LOGGER.info("Loading epoch time clock...");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }


}