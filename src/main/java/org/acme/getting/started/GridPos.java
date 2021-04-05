package org.acme.getting.started;

import java.util.ArrayList;

public class GridPos{

    //Maybe the arrayList needs to be thread-safe
    private ArrayList<User> users;

    public GridPos(){
        this.users = new ArrayList<User>();
    }

    public void add_user_grid(User user){
        users.add(user);
    }
}
