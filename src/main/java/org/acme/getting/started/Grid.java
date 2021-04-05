package org.acme.getting.started;

public class Grid {

    private static final int X_SIZE = 100;
    private static final int Y_SIZE = 100;
    private GridPos[][] grid;

    public Grid(){
        this.grid = new GridPos[X_SIZE][Y_SIZE];
    }

    public void store_position(int x, int y){
        GridPos gp = new GridPos();
        this.grid[x][y] = gp;

    }

}
