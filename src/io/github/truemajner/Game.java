package io.github.truemajner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    private int realEpoch = 0;

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
        boolean show = Config.waitTime >= 0 && Config.frequency > 0 && Config.skipTo <= epoch && step % Config.frequency == 0;
        if(Config.forceDisableShow) show = false;

        totalHP = 0;

        for (int i = 0; i < bots.size(); i ++) {
            Bot bot = bots.get(i);
            bot.run();

            if(show) totalHP += bot.getHealth();

            if(bot.getHealth() <= 0) {
                Map.getInstance().setCell(bot.getPosition(), Types.AIR);
                bots.remove(bot);
            }

            if(bot.getHealth() > MAX_HEALTH) bot.setHealth(99);

            if(bots.size() == 8) {
                return true;
            }

        }

        if(show) try {
            Thread.sleep(Config.waitTime);
            getArrayVisualization().updateCanvas();
            getArrayVisualization().updateStatisticLabels();
            if(!this.getArrayVisualization().finished()) waitCanvasFinish();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } else {
            //do nothing
        }

        return false;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public List<Stat> getStatistics () {
        return List.of(
                new Stat("Epoch", epoch),
                new Stat("Step", step),
                new Stat("Bot Alive", bots.size()),
                new Stat("AVG STEP", (int) (totalSteps / (epoch + 1))),
                new Stat("AVG HLT", totalHP / bots.size())
        );
    }

    private void createStartFood() {
        for(int i = 0; i < START_FOOD_COUNT; i ++) createFood();
        for(int i = 0; i < START_POISON_COUNT; i ++) createPoison();
    }

    private void next() {
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
            boolean result = iterate();
            step++;
            if(result) break;
        }
    }

    private void waitCanvasFinish() {
        while(!this.getArrayVisualization().finished()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {

            }
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

    public int getRealEpoch() {
        return realEpoch;
    }

    public void setRealEpoch(int realEpoch) {
        this.realEpoch = realEpoch;
    }

    public List<Bot> getBots() {
        return bots;
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

                    System.out.println("Epoch " + (epoch+realEpoch) + " avg " + String.valueOf(totalSteps / epoch).substring(0, Math.min(String.valueOf(totalSteps / epoch).length(), 6)) + " current " + step + " time " + last1000EpochCalculationTime);

                    last1000EpochCalculationTime = new Date().getTime();
                }

                if(step > bestScore) bestScore = step;

                saveResult();
            }

            bots = generateNewEpochBots();
        }
    }

    private List<Bot> generateNewEpochBots() {
        List<Bot> newBots = new ArrayList<>();

        for (int i = 0; i < BOT_COUNT_EPOCH_END; i ++) {
            Bot bot = bots.get(i);
            newBots.add(bot);
            for(int j = 0; j < BOT_COUNT_EPOCH_END - 1; j ++) {
                Bot newBot = bot.getClone();
                if(j >= BOT_COUNT_EPOCH_END - 1 - Config.getMutantCount()) {
                    newBot.mutate();
                    newBot.setEpochsSurvived(0);
                } //todo : now epochs by genome
                newBots.add(newBot);
            }
        }
        return newBots;
    }

    private void saveResult() {
        String result = "";
        for (int i = 0; i < BOT_COUNT_EPOCH_END; i ++) {
            result += Utils.join(bots.get(i).getGenome(), " ") + "\n";
        }

        result += "Survived : " + step;

        try {
            FileWriter writer = new FileWriter("./data/epoch" + (epoch+realEpoch) + ".txt");
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}