package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;
import xyz.velocity.modules.garrison.EnumBoostMode;

import java.util.List;

public class DataSave {

    @Getter
    String faction;

    @Getter
    double percentage;

    @Getter
    int captureTime;

    @Getter
    EnumBoostMode mode;

    @Getter
    List<BoostDataSave> boosts;

    boolean isNeutral;

    @Getter
    long protectionTime;

    public DataSave(String faction, double percentage, boolean isNeutral, int captureTime, EnumBoostMode mode, List<BoostDataSave> boosts, long protectionTime) {
        this.faction = faction;
        this.percentage = percentage;
        this.isNeutral = isNeutral;
        this.captureTime = captureTime;
        this.mode = mode;
        this.boosts = boosts;
        this.protectionTime = protectionTime;
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

    public void setMode(EnumBoostMode mode) {
        this.mode = mode;
    }

    public void setBoosts(List<BoostDataSave> boosts) {
        this.boosts = boosts;
    }

    public void setProtectionTime(long protectionTime) {
        this.protectionTime = protectionTime;
    }

}
