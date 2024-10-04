package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FishItemSave {

    String id;
    int reelChance;
    int amountToGive;
    int sellPrice;
    ItemMultiplierSave multiplier;
    String material;
    int data;
    boolean glow;
    String displayName;
    List<String> lore = new ArrayList<>();

    public FishItemSave(String id, int reelChance, int amountToGive, int sellPrice, ItemMultiplierSave multiplier, String material, int data, boolean glow, String displayName, List<String> lore) {
        this.id = id;
        this.reelChance = reelChance;
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
