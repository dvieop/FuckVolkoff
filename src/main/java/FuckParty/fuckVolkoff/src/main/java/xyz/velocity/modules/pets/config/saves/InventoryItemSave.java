package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class InventoryItemSave {

    String material;
    String displayName;
    List<String> lore;
    int data;
    int slot;

    public InventoryItemSave(String material, String displayName, List<String> lore, int data, int slot) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.data = data;
        this.slot = slot;
    }

}
