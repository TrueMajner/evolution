package io.github.truemajner.Components;

import io.github.truemajner.Config;
import io.github.truemajner.Game;
import io.github.truemajner.Utils;

import javax.swing.*;

public class SettingsPanel extends JPanel {
    private final JTextField mutantCountField;
    private final JTextField fixOutputUnsyncField;
    private final JTextField drawHealthField;
    private final JTextField saveResultsField;
    private final JTextField startFoodCountField;
    private final JTextField startPoisonCountField;
    private final JTextField maxEpochStepsField;

    public SettingsPanel () {
        JLabel mutantCountLabel = new JLabel("MUTANT_COUNT (0 - 7)");
        JLabel fixOutputUnsyncLabel = new JLabel("FixOutputUnsync (0 or 1)");
        JLabel drawHealthLabel = new JLabel("DrawHealth (0 or 1)");
        JLabel saveResultsLabel = new JLabel("SaveResults (0 or 1)");
        JLabel startFoodCountLabel = new JLabel("startFoodCount");
        JLabel startPoisonCountLabel = new JLabel("startPoisonCount");
        JLabel maxEpochStepsLabel = new JLabel("maxEpochSteps");

        mutantCountField = new JTextField(5);
        fixOutputUnsyncField = new JTextField(5);
        drawHealthField = new JTextField(5);
        saveResultsField = new JTextField(5);
        startFoodCountField = new JTextField(5);
        startPoisonCountField = new JTextField(5);
        maxEpochStepsField = new JTextField(5);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> updateData());

        this.add(mutantCountLabel);
        this.add(mutantCountField);

        this.add(fixOutputUnsyncLabel);
        this.add(fixOutputUnsyncField);

        this.add(drawHealthLabel);
        this.add(drawHealthField);

        this.add(saveResultsLabel);
        this.add(saveResultsField);

        this.add(startFoodCountLabel);
        this.add(startFoodCountField);

        this.add(startPoisonCountLabel);
        this.add(startPoisonCountField);

        this.add(maxEpochStepsLabel);
        this.add(maxEpochStepsField);

        this.add(sendButton);
    }

    private void updateData() {
        String mutantCountRaw = mutantCountField.getText();
        String fixOutputUnsyncRaw = fixOutputUnsyncField.getText();
        String drawHealthRaw = drawHealthField.getText();
        String saveResultsRaw = saveResultsField.getText();
        String startFoodCountRaw = startFoodCountField.getText();
        String startPoisonCountRaw = startPoisonCountField.getText();
        String maxEpochStepsRaw = maxEpochStepsField.getText();

        int mutantCount = Utils.isInteger(mutantCountRaw) ? Math.abs(Integer.parseInt(mutantCountRaw)) : Config.getMutantCount();
        int fixOutputUnsync = Utils.isInteger(fixOutputUnsyncRaw) ? Math.abs(Integer.parseInt(fixOutputUnsyncRaw)) : Config.isFixOutputUnsyncEnabled() ? 1 : 0;
        int drawHealth = Utils.isInteger(drawHealthRaw) ? Math.abs(Integer.parseInt(drawHealthRaw)) : Config.isDrawHealthEnabled() ? 1 : 0;
        int saveResults = Utils.isInteger(saveResultsRaw) ? Math.abs(Integer.parseInt(saveResultsRaw)) : Config.saveResultsEnabled() ? 1 : 0;
        int startFoodCount = Utils.isInteger(startFoodCountRaw) ? Math.abs(Integer.parseInt(startFoodCountRaw)) : Game.START_FOOD_COUNT;
        int startPoisonCount = Utils.isInteger(startPoisonCountRaw) ? Math.abs(Integer.parseInt(startPoisonCountRaw)) : Game.START_POISON_COUNT;
        int maxEpochSteps = Utils.isInteger(maxEpochStepsRaw) ? Math.abs(Integer.parseInt(maxEpochStepsRaw)) : Game.MAX_STEP_COUNT;

        if(mutantCount > 7) mutantCount = 7;
        if(fixOutputUnsync > 1) fixOutputUnsync = 1;
        if(drawHealth > 1) drawHealth = 1;
        if(saveResults > 1) saveResults = 1;
        if(startFoodCount > 200) startFoodCount = 200;
        if(startPoisonCount > 200) startPoisonCount = 200;

        Config.setMutantCount(mutantCount);
        Config.setFixOutputUnsync(fixOutputUnsync == 1);
        Config.setDrawHealth(drawHealth == 1);
        Config.setSaveResults(saveResults == 1);
        Game.START_FOOD_COUNT = startFoodCount;
        Game.START_POISON_COUNT = startPoisonCount;
        Game.MAX_STEP_COUNT = maxEpochSteps;
    }
}
