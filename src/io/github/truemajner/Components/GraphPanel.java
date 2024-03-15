package io.github.truemajner.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GraphPanel extends JPanel {
    private final String title;
    private final List<? extends Number> data;
    private boolean active;
    private static final int MARGIN = 40;
    private double minValue;
    private double maxValue;
    private final Timer graphUpdateTimer;

    public GraphPanel(String title, List<? extends Number> data, int updateInterval) {
        this.title = title;
        this.data = data;

        graphUpdateTimer = new Timer(updateInterval, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (isActive()) {
                    repaint();
                }
            }
        });
        graphUpdateTimer.start();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        g2d.drawLine(MARGIN, height - MARGIN, width - MARGIN, height - MARGIN);
        g2d.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);

        g2d.setColor(Color.BLUE);
        int dataSize = data.size() - 1;
        if(dataSize == 0) dataSize = 1;

        minValue = Double.MAX_VALUE;
        maxValue = Double.MIN_VALUE;
        for (Number num : data) {
            double val = num.doubleValue();
            minValue = Math.min(minValue, val);
            maxValue = Math.max(maxValue, val);
        }

        int intervalY = (height - 2 * MARGIN) / 10;
        for (int i = 0; i <= 10; i++) {
            double value = minValue + (maxValue - minValue) * i / 10;
            String valueStr = String.format("%.0f", value);
            int strWidth = g2d.getFontMetrics().stringWidth(valueStr);
            g2d.drawString(valueStr, MARGIN - strWidth - 5, height - MARGIN - i * intervalY + 5);
        }

        g2d.drawString(getTitle(), MARGIN - 5, height - MARGIN - 11 * intervalY + 5);

        double interval = (double) (width - 2 * MARGIN) / dataSize;
        double x = MARGIN;
        int y = height - MARGIN - scale(data.getFirst());

        for (int i = 1; i < dataSize; i++) {
            int newY = height - MARGIN - scale(data.get(i));
            g2d.drawLine((int) Math.round(x), y, (int) (Math.round(x + interval)), newY);
            x += interval;
            y = newY;
        }

        int numLabelsX = 10;
        double intervalX = (double) dataSize / numLabelsX;

        for (int i = 0; i <= numLabelsX; i++) {
            int xCoord = (int) Math.round(MARGIN + i * (((interval) * dataSize)) / numLabelsX);
            g2d.drawLine(xCoord, height - MARGIN + 5, xCoord, height - MARGIN - 5);
            String label = Integer.toString((int) Math.round(i * intervalX * 100));
            int strWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, xCoord - strWidth / 2, height - MARGIN + 20);
        }
    }

    public String getTitle() {
        return title;
    }

    private int scale(Number value) {
        double scaledValue = (value.doubleValue() - minValue) / (maxValue - minValue);
        int panelHeight = getHeight() - 2 * MARGIN;
        return (int) (scaledValue * panelHeight);
    }
}