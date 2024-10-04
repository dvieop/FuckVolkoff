package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;

public class DataSave {

    @Getter
    String faction;
    @Getter
    double percentage;
    @Getter
    int captureTime;

    boolean isNeutral;

    public DataSave(String faction, double percentage, boolean isNeutral, int captureTime) {
        this.faction = faction;
        this.percentage = percentage;
        this.isNeutral = isNeutral;
        this.captureTime = captureTime;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setNeutral(boolean neutral) {
        isNeutral = neutral;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }

}
