package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

@Getter
public class KitEnchant {

    String enchantName;
    boolean randomLevel;
    int maxLevel;
    int chance;

    public KitEnchant(String enchantName, boolean randomLevel, int maxLevel, int chance) {
        this.enchantName = enchantName;
        this.randomLevel = randomLevel;
        this.maxLevel = maxLevel;
        this.chance = chance;
    }

}
