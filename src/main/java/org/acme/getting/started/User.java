package org.acme.getting.started;

public class User {

    private String username;

    public User(String username){
        this.username = username;
    }

    public String get_name(){
        return this.username;
    }
}
