package xyz.kohara.adjcore.misc.capabilities;

public class BellTraderData implements IBellTraderData {

    private long lastBellDay = 0;

    @Override
    public long getLastBellDay() {
        return lastBellDay;
    }

    @Override
    public void setLastBellDay(long day) {
        this.lastBellDay = day;
    }
}
