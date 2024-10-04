package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;

@Getter
public class BoostSave {

    boolean enabled;
    String name;
    String displayName;
    int maxTier;
    double multiplierPerTier;
    int xpIncrement;

    public BoostSave(boolean enabled, String name, String displayName, int maxTier, double multiplierPerTier, int xpIncrement) {
        this.enabled = enabled;
        this.name = name;
        this.displayName = displayName;
        this.maxTier = maxTier;
        this.multiplierPerTier = multiplierPerTier;
        this.xpIncrement = xpIncrement;
    }


}
