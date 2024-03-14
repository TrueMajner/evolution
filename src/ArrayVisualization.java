
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ArrayVisualization extends JFrame {
    private static final int WIDTH = 976;
    private static final int HEIGHT = 600;

    private CanvasPanel canvasPanel;
    private JTextField skipToField;
    private JTextField waitTimeField;
    private JTextField frequencyField;
    private List<JLabel> statisticLabels;
    private Game game;
    private JTabbedPane tabbedPane;
    private GraphPanel avgDurationGraph;
    private GraphPanel stepsPerEpochGraph;
    private Timer graphUpdateTimer;

    public ArrayVisualization(Game game) {
        //todo : В отдельный поток
        this.game = game;
        setTitle("Evolution");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvasPanel = new CanvasPanel();
        JPanel inputPanel = createInputPanel();
        JPanel statisticPanel = createStatisticPanel(game.getStatistics().size());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Canvas", canvasPanel);
        tabbedPane.addTab("Graph", createGraphPanel(game));

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(statisticPanel, BorderLayout.NORTH);

        graphUpdateTimer = new Timer(250, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (tabbedPane.getSelectedIndex() == 1) {
                    avgDurationGraph.repaint();
                    stepsPerEpochGraph.repaint();
                }
            }
        });
        graphUpdateTimer.start();
    }

    private JPanel createGraphPanel(Game game) {
        JPanel graphPanel = new JPanel(new GridLayout(2, 1));

        List<Double> avgDurationData = game.getEpochData();
        avgDurationGraph = new GraphPanel("AVG vs 100 Epoch", avgDurationData);
        graphPanel.add(avgDurationGraph);

        List<Long> stepsPerEpochData = game.getEpochAvgData();
        stepsPerEpochGraph = new GraphPanel("AVG vs All time", stepsPerEpochData);
        graphPanel.add(stepsPerEpochGraph);

        return graphPanel;
    }

    private class GraphPanel extends JPanel {
        private String title;
        private List<? extends Number> data;

        public GraphPanel(String title, List<? extends Number> data) {
            this.title = title;
            this.data = data;
        }

        private static final int MARGIN = 40;
        private double minValue;
        private double maxValue;


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

            double interval = (double) (width - 2 * MARGIN) / dataSize;
            double x = MARGIN;
            int y = height - MARGIN - scale(data.get(0));

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

        private int scale(Number value) {
            double scaledValue = (value.doubleValue() - minValue) / (maxValue - minValue);
            int panelHeight = getHeight() - 2 * MARGIN;
            return (int) (scaledValue * panelHeight);
        }
    }

    private JPanel createStatisticPanel(int numStats) {
        JPanel panel = new JPanel(new GridLayout(1, numStats));
        statisticLabels = new ArrayList<>();
        for (int i = 0; i < numStats; i++) {
            JLabel label = new JLabel();
            panel.add(label);
            statisticLabels.add(label);
        }
        updateStatisticLabels();
        return panel;
    }

    public void updateStatisticLabels() {
        List<Stat> statistics = game.getStatistics();
        for (int i = 0; i < statistics.size(); i++) {
            Stat stat = statistics.get(i);
            statisticLabels.get(i).setText(stat.getLabel() + ": " + stat.getValue());
        }
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        JLabel skipToLabel = new JLabel("Пропуск до:");
        JLabel waitTimeLabel = new JLabel("Ожидание:");
        JLabel frequencyLabel = new JLabel("Частота:");

        skipToField = new JTextField(5);
        waitTimeField = new JTextField(5);
        frequencyField = new JTextField(5);

        JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToBackend();
            }
        });

        panel.add(skipToLabel);
        panel.add(skipToField);
        panel.add(waitTimeLabel);
        panel.add(waitTimeField);
        panel.add(frequencyLabel);
        panel.add(frequencyField);
        panel.add(sendButton);

        return panel;
    }


    private void sendDataToBackend() {
        String skipToRaw = skipToField.getText();
        String waitTimeRaw = waitTimeField.getText();
        String frequencyRaw = frequencyField.getText();

        if(!Utils.isInteger(skipToRaw)) skipToRaw = String.valueOf(Config.getSkipTo());
        if(!Utils.isInteger(waitTimeRaw)) waitTimeRaw = String.valueOf(Config.getWaitTime());
        if(!Utils.isInteger(frequencyRaw)) frequencyRaw = String.valueOf(Config.getFrequency());

        int skipTo = Math.abs(Integer.parseInt(skipToRaw));
        int waitTime = Math.abs(Integer.parseInt(waitTimeRaw));
        int frequency = Math.abs(Integer.parseInt(frequencyRaw));

        if(frequency < 1) frequency = 1;

        System.out.println("Skip to: " + skipTo + ", Wait time: " + waitTime + ", Frequency: " + frequency);

        Config.setSkipTo(skipTo);
        Config.setWaitTime(waitTime);
        Config.setFrequency(frequency);
    }

    public void updateCanvas() {
        canvasPanel.repaint();
    }

    private class CanvasPanel extends JPanel {
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
}
