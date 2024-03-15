package io.github.truemajner;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.util.Objects;

public class Main {

    static String dataFolderPath = "./data";
    static File dataFolder = new File(dataFolderPath);

    public static void main(String[] args) {
        createDataDirectory();

        if(!Config.isFixOutputUnsyncEnabled() && Config.isDrawHealthEnabled()) {
            Utils.printWarning("Drawing health is enabled without FixOutputUnsync. This may cause unexpected errors.");
        }

        Game game = new Game();
        ArrayVisualization arrayVisualization = new ArrayVisualization(game);
        arrayVisualization.setVisible(true);
        game.setArrayVisualization(arrayVisualization);
        if(Config.isLoadLast()) loadGenome(game);
        game.start();
    }

    static void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(dataFolderPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void loadGenome(Game game) {
        File dataFile = null;
        int maxEpoch = 0;

        for(File file : Objects.requireNonNull(dataFolder.listFiles())) {
            String fileName = file.getName();
            if(fileName.endsWith(".txt") && fileName.length() > 5) {
                int epoch = Integer.parseInt(fileName.substring(5, fileName.indexOf(".txt")));
                if(epoch > maxEpoch) {
                    maxEpoch = epoch;
                    dataFile = file;
                }
            }
        }

        game.setRealEpoch(maxEpoch);

        List<byte[]> listOfLists = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile.getPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Survived")) {
                    game.setBestScore(Integer.parseInt(line.split(": ")[1]));
                    break;
                }
                byte[] numbers = new byte[64];
                String[] tokens = line.trim().split("\\s+");

                for(int i = 0; i < tokens.length; i ++) {
                    String token = tokens[i];
                    numbers[i] = Byte.parseByte(token);
                }

                listOfLists.add(numbers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int kf = 0;
        for (byte[] genome : listOfLists) {
            for(int i = 0; i < 8; i ++) {
                game.bots.get(kf * 8 + i).setGenome(genome);
            }
            kf++;
        }

    }
}