package io.github.truemajner;

public class Bot {
    Position position;
    int epochsSurvived;
    int direction;
    int health;
    int stamina;
    int pointer;

    private Game game;
    public void mutate () {
        for(int i = 0; i < 2; i ++) {
            int genIndex = Utils.getRandomInteger(0, 64);
            byte genValue = Utils.getRandomByte(0, 64);
            this.setGen(genIndex, genValue);
        }
    }

    public Bot getClone() {
        return new Bot(
                this.getPosition().getClone(),
                this.getEpochsSurvived(),
                this.getDirection(),
                this.getHealth(),
                this.getStamina(),
                this.getPointer(),
                this.getGame(),
                this.genome.clone()
        );
    }

    private static Position[] DIRECTIONS = {
            new Position(-1, -1),
            new Position(0, -1),
            new Position(1, -1),
            new Position(1, 0),
            new Position(1, 1),
            new Position(0, 1),
            new Position(-1, 1),
            new Position(-1, 0),
    };

    byte[] genome = new byte[64];

    private byte randomGen() {
        return Utils.getRandomByte(0, 64);
    }

    private void setGen(int index, byte value) {
        this.getGenome()[index] = value;
    }

    private void randomGenome() {
        for(int i = 0; i < this.getGenome().length; i ++) {
            //setGen(i, (byte)0);
            setGen(i, randomGen());
        }
    }

    public Bot(Position position, int epochsSurvived, int direction, int health, int stamina, int pointer, Game game, byte[] genome) {
        this.position = position;
        this.epochsSurvived = epochsSurvived;
        this.direction = direction;
        this.health = health;
        this.stamina = stamina;
        this.pointer = pointer;
        this.game = game;
        this.genome = genome;
    }

    public Bot(Game game) {
        this.game = game;
        randomGenome();
        setPosition(Position.randomEmptyPosition());
        game.map.setCell(this.getPosition(), Types.BOT);
        setEpochsSurvived(0);
        setDirection(0);
        setHealth(10);
        setStamina(10);
        setPointer(0);
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public byte getGen(int index) {
        return this.getGenome()[index];
    }

    private byte getCurrentGen() {
        return this.getGen(this.getPointer());
    }

    private Game getGame() {
        return game;
    }

    private void setGame(Game game) {
        this.game = game;
    }

    public int getX() {
        return this.getPosition().getX();
    }

    public int getY() {
        return this.getPosition().getY();
    }

    private void increasePointer(int value) {
        this.setPointer(this.getPointer() + value);
    }

    private void addToHealth(int value) {
        this.setHealth(this.getHealth() + value);
    }
    private void addToStamina(int value) {
        this.setStamina(this.getStamina() + value);
    }
    //todo : мб использовать делегаты или что-то ещё?
    private void move(byte gen) {
        this.setStamina(0);

        Position direction = DIRECTIONS[(gen + getDirection()) % 8];
        Position target = this.getPosition().getClone();
        target.moveByPosition(direction);
        int cellValue = Map.getInstance().getCell(target);

        increasePointer(cellValue);

        switch (cellValue) {
            case Types.AIR:
                Map.getInstance().setCell(this.getPosition(), Types.AIR);
                Map.getInstance().setCell(target, Types.BOT);
                this.setPosition(target);
                break;

            case Types.WALL:

                break;

            case Types.FOOD:
                Map.getInstance().setCell(this.getPosition(), Types.AIR);
                Map.getInstance().setCell(target, Types.BOT);
                this.setPosition(target);
                this.addToHealth(10);
                this.getGame().spawnFP();

                break;

            case Types.POISON:
                Map.getInstance().setCell(this.getPosition(), Types.AIR);
                this.setHealth(0);
                break;

            case Types.BOT:

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + cellValue);
        }
    }

    private void eat(byte gen) {
        this.setStamina(0);

        Position direction = DIRECTIONS[(gen + getDirection()) % 8];
        Position target = this.getPosition().getClone();
        target.moveByPosition(direction);
        int cellValue = Map.getInstance().getCell(target);

        increasePointer(cellValue);

        switch (cellValue) {
            case Types.AIR:

                break;

            case Types.WALL:

                break;

            case Types.FOOD:
                Map.getInstance().setCell(target, Types.AIR);
                this.addToHealth(10);
                this.getGame().spawnFP();
                break;

            case Types.POISON:
                Map.getInstance().setCell(target, Types.FOOD);
                break;

            case Types.BOT:

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + cellValue);
        }
    }

    private void rotate(byte gen) { //todo : мб сделать поворот, а не установку направления? Типа сумма и остаток от деления
        this.addToStamina(-1);
        increasePointer(1);
        this.setDirection(gen);
    }

    private void view(byte gen) {
        this.addToStamina(-1);

        Position direction = DIRECTIONS[(gen + getDirection()) % 8];
        Position target = this.getPosition().getClone();
        target.moveByPosition(direction);
        int cellValue = Map.getInstance().getCell(target);

        increasePointer(cellValue);
    }

    private void jump(Byte gen) {
        this.addToStamina(-1);
        this.increasePointer(gen);
    }

    public void run() {
        while(this.getStamina() > 0) next();
        this.addToHealth(-1);
        this.setStamina(10);
    }

    public void next() {
        byte gen = getCurrentGen();
        if(gen < 8) {
            this.move(gen);
        } else if(gen < 16) {
            this.eat(gen);
        } else if(gen < 24) {
            this.rotate(gen);
        } else if(gen < 32) {
            this.view(gen);
        } else if(gen < 64) {
            this.jump(gen);
        }

        if(getPointer() >= 64) setPointer(getPointer() % 64);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getEpochsSurvived() {
        return epochsSurvived;
    }

    public void setEpochsSurvived(int epochsSurvived) {
        this.epochsSurvived = epochsSurvived;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public byte[] getGenome() {
        return genome;
    }

    public void setGenome(byte[] genome) {
        this.genome = genome;
    }

    /*private io.github.truemajner.Position convert() {

    }*/
}
