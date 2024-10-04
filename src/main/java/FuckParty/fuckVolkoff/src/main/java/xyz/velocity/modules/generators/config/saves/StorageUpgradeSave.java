package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;

@Getter
public class StorageUpgradeSave {

    boolean enabled;
    int maxLevel;
    int upgradeCostIncrement;
    int storageUpgradeIncrement;

    public StorageUpgradeSave(boolean enabled, int maxLevel, int upgradeCostIncrement, int storageUpgradeIncrement) {
        this.enabled = enabled;
        this.maxLevel = maxLevel;
        this.upgradeCostIncrement = upgradeCostIncrement;
        this.storageUpgradeIncrement = storageUpgradeIncrement;
    }

}
