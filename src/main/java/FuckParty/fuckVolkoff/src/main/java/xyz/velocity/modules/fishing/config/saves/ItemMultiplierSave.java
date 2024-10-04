package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

@Getter
public class ItemMultiplierSave {

    boolean enabled;
    double chance;
    int amount;

    public ItemMultiplierSave(boolean enabled, double chance, int amount) {
        this.enabled = enabled;
        this.chance = chance;
        this.amount = amount;
    }

}
