package org.acme.utils;

import org.acme.getting.started.model.LocationReport;
import org.acme.getting.started.persistence.User;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Util {
    /*
The resource URL is not working in the JAR
If we try to access a file that is inside a JAR,
It throws NoSuchFileException (linux), InvalidPathException (Windows)

Resource URL Sample: file:java-io.jar!/json/file1.json
*/

    public static HashMap<String, HashMap<Integer, LocationReport>> mapUsersFromMongoToJavaObjects(List<User> users) {
        HashMap<String, HashMap<Integer, LocationReport>> usersJavaObj = new HashMap<>();
        for(int i = 0; i < users.size(); i++) {
            String username = users.get(i).getUsername();
            List<LocationReport> locationReports = users.get(i).locationReports;
            HashMap<Integer, LocationReport> locationReportHashMap = new HashMap<>();
            for(int j = 0; j < locationReports.size(); j++) {
                locationReportHashMap.put(locationReports.get(j).epoch, locationReports.get(j));
            }
            usersJavaObj.put(username, locationReportHashMap);
        }
        return usersJavaObj;
    }

    public File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    public static InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = Util.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

}
