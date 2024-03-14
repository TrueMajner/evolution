public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        Position position = randomPosition();
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public String toString() {
        return "X : " + this.getX() + ", Y : " + this.getY();
    }

    public Position getClone() {
        return new Position(this.getX(), this.getY());
    }

    public static Position randomPosition() {
        int x = Utils.getRandomInteger(0, Map.SIZE_X);
        int y = Utils.getRandomInteger(0, Map.SIZE_Y);
        return new Position(x, y);
    }

    //do not use before Map init
    public static Position randomEmptyPosition() {
        Position position;
        do {
            position = randomPosition();
        } while (Map.getInstance().getCell(position) != Types.AIR);
        return position;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveX(int delta) {
        this.setX(this.getX() + delta);
    }

    public void moveY(int delta) {
        this.setY(this.getY() + delta);
    }

    public void moveByPosition(Position position) {
        this.moveX(position.getX());
        this.moveY(position.getY());
    }
}
