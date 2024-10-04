package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class GeneratorSave {

    String id;
    TierUpgradeSave tierUpgrade;
    StorageUpgradeSave storageUpgrade;
    List<String> hologram;
    GenItemSave item;

    public GeneratorSave(String id, TierUpgradeSave tierUpgrade, StorageUpgradeSave storageUpgrade, List<String> hologram, GenItemSave item) {
        this.id = id;
        this.tierUpgrade = tierUpgrade;
        this.storageUpgrade = storageUpgrade;
        this.hologram = hologram;
        this.item = item;
    }

}
