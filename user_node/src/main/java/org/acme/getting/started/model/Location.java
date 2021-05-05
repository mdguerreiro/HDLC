package org.acme.getting.started.model;

public class Location {
    private int x;
    private int y;
    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int get_X(){
        return this.x;
    }

    public int get_Y(){
        return this.y;
    }
}
