package io.github.truemajner;

public class Map { //todo : make not singleton
    private static Map instance;
    public static int SIZE_X = 48;
    public static int SIZE_Y = 24;
    Package src;
    private int[][] map = new int[SIZE_X][SIZE_Y];

    public int getCell(int x, int y) {
        return this.getMap()[x][y];
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public void resetMap () {
        for(int x = 0; x < SIZE_X; x ++) {
            for(int y = 0; y < SIZE_Y; y ++) {
                map[x][y] = Types.AIR;
                if(x == 0 || y == 0 || x == 47 || y == 23 || (x == 12 && (y > 0 && y < 9)) || (x == 36 && (y > 11 && y < 19))) map[x][y] = Types.WALL;
            }
        }
    }

    public void setCell(int x, int y, int type) {
        this.getMap()[x][y] = type;
    }

    public void setCell(Position position, int type) {
        this.getMap()[position.getX()][position.getY()] = type;
    }

    public int getCell(Position position) {
        return this.getCell(position.getX(), position.getY());
    }

    private Map() {
        resetMap();
    }

    public static Map getInstance() {
        if(instance == null) {
            instance = new Map();
        }
        return instance;
    }
}
