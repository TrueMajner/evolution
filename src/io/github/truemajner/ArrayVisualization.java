package io.github.truemajner;

import io.github.truemajner.Components.CanvasPanel;
import io.github.truemajner.Components.GraphPanel;
import io.github.truemajner.Config;
import io.github.truemajner.Game;
import io.github.truemajner.Stat;
import io.github.truemajner.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ArrayVisualization extends JFrame {
    private static final int WIDTH = 976;
    private static final int HEIGHT = 600;

    private Game game;
    private CanvasPanel canvasPanel;
    private JTextField skipToField;
    private JTextField waitTimeField;
    private JTextField frequencyField;
    private List<JLabel> statisticLabels;
    private JTabbedPane tabbedPane;
    private GraphPanel avgDurationGraph;
    private GraphPanel stepsPerEpochGraph;

    public ArrayVisualization(Game game) {
        this.game = game;
        setTitle("Evolution");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvasPanel = new CanvasPanel(game);
        JPanel inputPanel = createInputPanel();
        JPanel statisticPanel = createStatisticPanel(game.getStatistics().size());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Canvas", canvasPanel);
        tabbedPane.addTab("Graph", createGraphPanel(game));

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(statisticPanel, BorderLayout.NORTH);

        tabbedPane.addChangeListener(e -> {
            avgDurationGraph.setActive(tabbedPane.getSelectedIndex() == 1);
            stepsPerEpochGraph.setActive(tabbedPane.getSelectedIndex() == 1);
        });
    }

    public boolean finished() {
        return this.canvasPanel.isFinished();
    }

    private JPanel createGraphPanel(Game game) {
        JPanel graphPanel = new JPanel(new GridLayout(2, 1));

        List<Double> avgDurationData = game.getEpochData();
        avgDurationGraph = new GraphPanel("Average Epoch Duration for Last 100 Epochs (One Value per 100 Epochs)", avgDurationData, 250);
        graphPanel.add(avgDurationGraph);

        List<Long> stepsPerEpochData = game.getEpochAvgData();
        stepsPerEpochGraph = new GraphPanel("Average Epoch Duration for All Previous Epochs (One Value per 100 Epochs)", stepsPerEpochData, 250);
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
        JLabel skipToLabel = new JLabel("Skip to:");
        JLabel waitTimeLabel = new JLabel("Wait time:");
        JLabel frequencyLabel = new JLabel("Frequency:");

        skipToField = new JTextField(5);
        waitTimeField = new JTextField(5);
        frequencyField = new JTextField(5);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendDataToBackend());

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

        int skipTo = Utils.isInteger(skipToRaw) ? Math.abs(Integer.parseInt(skipToRaw)) : Config.getSkipTo();
        int waitTime = Utils.isInteger(waitTimeRaw) ? Math.abs(Integer.parseInt(waitTimeRaw)) : Config.getWaitTime();
        int frequency = Utils.isInteger(frequencyRaw) ? Math.abs(Integer.parseInt(frequencyRaw)) : Config.getFrequency();

        frequency = Math.max(frequency, 1);

        System.out.println("Skip to: " + skipTo + ", Wait time: " + waitTime + ", Frequency: " + frequency);

        Config.setSkipTo(skipTo);
        Config.setWaitTime(waitTime);
        Config.setFrequency(frequency);
    }

    public void updateCanvas() {
        canvasPanel.repaint();
    }
}
