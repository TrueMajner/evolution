public class Config {
    static boolean forceDisableShow = false;
    static int waitTime = 1000;
    static int skipTo = 0;
    static int frequency = 1;

    public static boolean isForceDisableShow() {
        return forceDisableShow;
    }

    public static void setForceDisableShow(boolean forceDisableShow) {
        Config.forceDisableShow = forceDisableShow;
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
