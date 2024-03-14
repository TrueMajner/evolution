import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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