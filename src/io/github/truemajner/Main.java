package io.github.truemajner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        createDataDirectory();

        Game game = new Game();
        ArrayVisualization arrayVisualization = new ArrayVisualization(game);
        arrayVisualization.setVisible(true);
        game.setArrayVisualization(arrayVisualization);
        game.start();
    }

    static void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get("./data"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}