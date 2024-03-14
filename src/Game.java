import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Game {
    int epoch = 0;
    int step = 0;
    long totalSteps = 0;
    List<Bot> bots = new ArrayList<>();
    Map map = Map.getInstance();
    public static int START_FOOD_COUNT = 60;//60;
    public static int START_POISON_COUNT = 60;//60;
    public static double FOOD_CHANCE = (double) START_FOOD_COUNT / ((double) START_FOOD_COUNT + (double) START_POISON_COUNT);
    public static int START_BOT_COUNT = 64;
    public static int MAX_STEP_COUNT = 10000;
    public static int MAX_HEALTH = 99;
    public static int BOT_COUNT_EPOCH_END = 8;
    public static int MAX_EPOCH_COUNT = Integer.MAX_VALUE;
    private int bestScore = 0;
    private ArrayVisualization arrayVisualization;

    private ArrayVisualization getArrayVisualization() {
        return arrayVisualization;
    }

    public void setArrayVisualization(ArrayVisualization arrayVisualization) {
        this.arrayVisualization = arrayVisualization;
    }

    public void spawnFP() {
        Position position = Position.randomEmptyPosition();
        int type = Math.random() < FOOD_CHANCE ? Types.FOOD : Types.POISON;
        map.setCell(position, type);
    }

    private void createFood() {
        Position position = Position.randomEmptyPosition();
        map.setCell(position, Types.FOOD);
    }

    private void createPoison() {
        Position position = Position.randomEmptyPosition();
        map.setCell(position, Types.POISON);
    }

    private int totalHP = 0;
    boolean iterate() {
        totalHP = 0;

        for (int i = 0; i < bots.size(); i ++) {
            Bot bot = bots.get(i);
            bot.run();

            totalHP += bot.getHealth();

            //System.out.println("epoch " + epoch + " step " + step + " bot " + i + " of " + bots.size() + " gen " + bot.getGen(bot.getPointer()) + " health " + bot.getHealth());

            if(bot.getHealth() <= 0) {
                Map.getInstance().setCell(bot.getPosition(), Types.AIR);
                bots.remove(bot);
            }

            if(bot.getHealth() > MAX_HEALTH) bot.setHealth(99);

            if(bots.size() == 8) {
                return true;
            }

        }

        boolean show = Config.waitTime >= 0 && Config.frequency > 0 && Config.skipTo <= epoch && step % Config.frequency == 0;
        if(Config.forceDisableShow) show = false;

        if(show) try {
            Thread.sleep(Config.waitTime);
            getArrayVisualization().updateCanvas();
            getArrayVisualization().updateStatisticLabels();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } else {
            //do nothing
        }

        return false;
    }
    public List<Stat> getStatistics () {
        //todo : precalc
        return List.of(
                new Stat("Epoch", epoch),
                new Stat("Step", step),
                new Stat("Bot Alive", bots.size()),
                new Stat("AVG STEP", (int) (totalSteps / (epoch + 1))),
                new Stat("AVG HLT", totalHP / bots.size())
        );
    }

    void createStartFood() {
        for(int i = 0; i < START_FOOD_COUNT; i ++) createFood();
        for(int i = 0; i < START_POISON_COUNT; i ++) createPoison();
    }

    void next() {
        step = 0;
        map.resetMap();

        for(int i = 0; i < bots.size(); i ++) {
            Bot bot = bots.get(i);
            bot.setPosition(Position.randomEmptyPosition());
            Map.getInstance().setCell(bot.getPosition(), Types.BOT);
            bot.setPointer(0);
            bot.setHealth(10);
            bot.setStamina(10);
            bot.setDirection(0);
            bot.setEpochsSurvived(bot.getEpochsSurvived() + 1);
        }

        createStartFood();

        while(step < MAX_STEP_COUNT) {
            //System.out.println("epoch " + epoch + " step " + step + " bot count " + bots.size());
            boolean result = iterate();
            step++;
            if(result) break;
        }
    }

    public Game() {
        map.resetMap();

        for(int i = 0; i < START_BOT_COUNT; i ++) {
            bots.add(new Bot(this));
        }
    }

    private List<Double> epochData = new ArrayList<>();
    private List<Long> epochAvgData = new ArrayList<>();

    public List<Long> getEpochAvgData() {
        return epochAvgData;
    }

    public int getEpoch() {
        return epoch;
    }

    public long getTotalSteps() {
        return totalSteps;
    }

    public List<Double> getEpochData() {
        return epochData;
    }

    void start() {
        epoch = 0;
        epochData.add(0d);
        epochAvgData.add(0L);
        long last1000EpochCalculationTime = new Date().getTime();

        while (epoch < MAX_EPOCH_COUNT) {
            totalSteps += step;
            next();
            epoch ++;

            if(epoch % 100 == 0) {
                epochData.add((double) (step / 100));
                epochAvgData.add((long) ((double) totalSteps/(double) epoch));
            } else epochData.set(epochData.size() - 1, epochData.getLast() + (double) step / 100);

            if(step > bestScore || epoch % 1000 == 0) {

                if(epoch % 1000 == 0) {
                    last1000EpochCalculationTime = new Date().getTime() - last1000EpochCalculationTime;

                    System.out.println("Epoch " + epoch + " avg " + String.valueOf(totalSteps / epoch).substring(0, Math.min(String.valueOf(totalSteps / epoch).length(), 6)) + " current " + step + " time " + last1000EpochCalculationTime);

                    last1000EpochCalculationTime = new Date().getTime();
                }

                if(step > bestScore) bestScore = step;
                String result = "";
                for (int i = 0; i < BOT_COUNT_EPOCH_END; i ++) {
                    result += Utils.join(bots.get(i).getGenome(), " ") + "\n";
                }

                result += "Survived : " + step;

                FileWriter writer = null;
                try {
                    writer = new FileWriter("./data/epoch" + epoch + ".txt");
                    writer.write(result);
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            List<Bot> newBots = new ArrayList<>();

            for (int i = 0; i < BOT_COUNT_EPOCH_END; i ++) {
                Bot bot = bots.get(i);
                newBots.add(bot);
                for(int j = 0; j < 7; j ++) { //todo : переделать и вынести константу MUTANT_COUNT
                    Bot newBot = bot.getClone();
                    if(j > 4) {
                        newBot.mutate();
                        newBot.setEpochsSurvived(0);
                    } //todo : now epochs by genome
                    newBots.add(newBot);
                    //newBot.randomGenome();
                }
            }

            bots = newBots;
        }
    }
}