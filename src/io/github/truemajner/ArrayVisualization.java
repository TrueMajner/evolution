package io.github.truemajner;

import io.github.truemajner.Components.CanvasPanel;
import io.github.truemajner.Components.GraphPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                avgDurationGraph.setActive(tabbedPane.getSelectedIndex() == 1);
                stepsPerEpochGraph.setActive(tabbedPane.getSelectedIndex() == 1);
            }
        });
    }

    private JPanel createGraphPanel(Game game) {
        JPanel graphPanel = new JPanel(new GridLayout(2, 1));

        List<Double> avgDurationData = game.getEpochData();
        avgDurationGraph = new GraphPanel("Средняя продожительность эпохи за последние 100 эпох. (Одно значение каждые 100 эпох)", avgDurationData, 250);
        graphPanel.add(avgDurationGraph);

        List<Long> stepsPerEpochData = game.getEpochAvgData();
        stepsPerEpochGraph = new GraphPanel("Средняя продолжительность эпохи за все прежние эпохи. (Одно значение каждые 100 эпох)", stepsPerEpochData, 250);
        graphPanel.add(stepsPerEpochGraph);

        return graphPanel;
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


}
