package org.acme.getting.started.persistence;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

import org.acme.getting.started.model.LocationReport;
import org.bson.types.ObjectId;

import java.util.List;

@MongoEntity(collection="user")
public class User extends PanacheMongoEntityBase {

    ObjectId _id;

    private String username;
    public List<LocationReport> locationReports;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}