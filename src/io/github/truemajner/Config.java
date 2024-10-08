package io.github.truemajner;

public class Config {
    static boolean forceDisableShow = false;
    static int waitTime = 1000;
    static int skipTo = 0;
    static int frequency = 1;
    static boolean loadLast = false;
    static int MUTANT_COUNT = 4;
    static boolean FixOutputUnsync = true;
    static boolean DrawHealth = true;
    static boolean SaveResults = true;

    public static boolean saveResultsEnabled() {
        return SaveResults;
    }

    public static boolean isFixOutputUnsyncEnabled() {
        return FixOutputUnsync;
    }

    public static boolean isDrawHealthEnabled() {
        return DrawHealth;
    }

    public static int getMutantCount() {
        return MUTANT_COUNT;
    }

    public static boolean isLoadLast() {
        return loadLast;
    }

    public static boolean isForceDisableShow() {
        return forceDisableShow;
    }

    public static void setForceDisableShow(boolean forceDisableShow) {
        Config.forceDisableShow = forceDisableShow;
    }

    public static void setMutantCount(int mutantCount) {
        MUTANT_COUNT = mutantCount;
    }

    public static void setFixOutputUnsync(boolean fixOutputUnsync) {
        FixOutputUnsync = fixOutputUnsync;
    }

    public static void setDrawHealth(boolean drawHealth) {
        DrawHealth = drawHealth;
    }

    public static void setSaveResults(boolean saveResults) {
        SaveResults = saveResults;
    }

    public static int getWaitTime() {
        return waitTime;
    }

    public static void setWaitTime(int waitTime) {
        Config.waitTime = waitTime;
    }

    public static int getSkipTo() {
        return skipTo;
    }

    public static void setSkipTo(int skipTo) {
        Config.skipTo = skipTo;
    }

    public static int getFrequency() {
        return frequency;
    }

    public static void setFrequency(int frequency) {
        Config.frequency = frequency;
    }
}
