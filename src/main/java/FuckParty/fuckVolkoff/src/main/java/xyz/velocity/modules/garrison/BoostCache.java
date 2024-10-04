package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import lombok.Getter;
import xyz.velocity.modules.garrison.config.saves.BoostSave;

@Getter
public class BoostCache {

    BoostSave boost;
    int tier;
    double multiplier;
    int xp;
    int xpTillUpgrade;

    public BoostCache(BoostSave boostSave, int tier, double multiplier, int xp, int xpTillUpgrade) {
        this.boost = boostSave;
        this.tier = tier;
        this.multiplier = multiplier + 1;
        this.xp = xp;
        this.xpTillUpgrade = xpTillUpgrade;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setXpTillUpgrade(int xpTillUpgrade) {
        this.xpTillUpgrade = xpTillUpgrade;
    }

    public void addXp(int xp) {
        this.xp += xp;
    }

}
