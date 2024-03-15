package io.github.truemajner.Components;

import javax.swing.*;
import java.awt.*;
import io.github.truemajner.Map;

public class CanvasPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int[][] map = Map.getInstance().getMap();

        int rows = map.length;
        int cols = map[0].length;

        int cellSize = Math.min(getWidth() / cols, 2 * (getHeight() / rows));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (map[i][j]) {
                    case 4:
                        g2d.setColor(Color.RED);
                        break;
                    case 2:
                        g2d.setColor(Color.GRAY);
                        break;
                    case 5:
                        g2d.setColor(Color.BLUE);
                        break;
                    case 3:
                        g2d.setColor(Color.GREEN);
                        break;
                    case 1:
                        g2d.setColor(Color.BLACK);
                        break;
                    default:
                        g2d.setColor(Color.WHITE);
                        break;
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }
}