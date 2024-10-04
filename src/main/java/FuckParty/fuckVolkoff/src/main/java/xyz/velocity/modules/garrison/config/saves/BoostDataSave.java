package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;
import xyz.velocity.modules.garrison.EnumBoostMode;

@Getter
public class BoostDataSave {

    String name;
    int tier;
    double multiplier;
    int xp;
    int xpTillUpgrade;

    public BoostDataSave(String name, int tier, double multiplier, int xp, int xpTillUpgrade) {
        this.name = name;
        this.tier = tier;
        this.multiplier = multiplier;
        this.xp = xp;
        this.xpTillUpgrade = xpTillUpgrade;
    }

}
