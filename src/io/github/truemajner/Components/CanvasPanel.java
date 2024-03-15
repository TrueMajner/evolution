package io.github.truemajner.Components;

import javax.swing.*;
import java.awt.*;

import io.github.truemajner.Bot;
import io.github.truemajner.Config;
import io.github.truemajner.Map;
import io.github.truemajner.Game;

public class CanvasPanel extends JPanel {
    private Game game;
    private boolean finished;

    public CanvasPanel(Game game) {
        this.game = game;
        setFinished(true);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setFinished(false);
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

        if(Config.isDrawHealthEnabled()) for(int i = 0; i < game.getBots().size(); i ++) {
            Bot bot = game.getBots().get(i);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setColor(Color.WHITE);
            g2d.setFont(g2d.getFont().deriveFont((float)cellSize * 0.9f));
            int textWidth = g2d.getFontMetrics().stringWidth(String.valueOf(bot.getHealth()));
            int textHeight = g2d.getFontMetrics().getHeight();

            int textX = bot.getX() * cellSize + (cellSize - textWidth) / 2;
            int textY = (bot.getY() + 1) * cellSize - textHeight / 2 + cellSize / 2;

            g2d.drawString(String.valueOf(bot.getHealth()), textX, textY);
        }

        setFinished(true);
    }
}