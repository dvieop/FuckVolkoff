package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;
import xyz.velocity.modules.fishing.config.saves.ItemMultiplierSave;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DropItemSave {

    String id;
    int dropChance;
    int amountToGive;
    int sellPrice;
    ItemMultiplierSave multiplier;
    String material;
    int data;
    boolean glow;
    String displayName;
    List<String> lore = new ArrayList<>();

    public DropItemSave(String id, int dropChance, int amountToGive, int sellPrice, ItemMultiplierSave multiplier, String material, int data, boolean glow, String displayName, List<String> lore) {
        this.id = id;
        this.dropChance = dropChance;
        this.amountToGive = amountToGive;
        this.sellPrice = sellPrice;
        this.multiplier = multiplier;
        this.material = material;
        this.data = data;
        this.glow = glow;
        this.displayName = displayName;
        this.lore = lore;
    }

}
