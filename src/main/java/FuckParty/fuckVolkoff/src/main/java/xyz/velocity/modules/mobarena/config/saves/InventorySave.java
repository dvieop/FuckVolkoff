package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;

import java.util.List;

@Getter
public class InventorySave {

    String guiName;
    int size;
    List<InventoryItemSave> classItems;
    InventoryItemSave filler;

    public InventorySave(String guiName, int size, List<InventoryItemSave> classItems, InventoryItemSave filler) {
        this.guiName = guiName;
        this.size = size;
        this.classItems = classItems;
        this.filler = filler;
    }

}
