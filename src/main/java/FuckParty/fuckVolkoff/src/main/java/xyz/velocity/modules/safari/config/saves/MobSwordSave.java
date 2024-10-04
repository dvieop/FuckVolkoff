package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class MobSwordSave {

    String material;
    String displayName;
    List<String> lore;
    List<String> enchants;
    boolean luckboost;
    int mobsToKill;
    double increase;
    double maxChanceToIncrease;

    public MobSwordSave(String material, String displayName, List<String> lore, List<String> enchants, boolean luckboost, int mobsToKill, double increase, double maxChanceToIncrease) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.enchants = enchants;
        this.luckboost = luckboost;
        this.mobsToKill = mobsToKill;
        this.increase = increase;
        this.maxChanceToIncrease = maxChanceToIncrease;
    }

}
