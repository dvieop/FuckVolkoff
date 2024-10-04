package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;

@Getter
public class TierUpgradeSave {

    boolean enabled;
    int maxTier;
    int upgradeCostIncrement;
    int baseMoney;
    int speedIncrement;

    public TierUpgradeSave(boolean enabled, int maxTier, int upgradeCostIncrement, int baseMoney, int speedIncrement) {
        this.enabled = enabled;
        this.maxTier = maxTier;
        this.upgradeCostIncrement = upgradeCostIncrement;
        this.baseMoney = baseMoney;
        this.speedIncrement = speedIncrement;
    }

}
