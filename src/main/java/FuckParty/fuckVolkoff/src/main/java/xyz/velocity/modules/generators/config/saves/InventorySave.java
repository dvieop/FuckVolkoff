package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;

import java.util.List;

@Getter
public class InventorySave {

    String guiName;
    int size;
    InventoryItemSave mainItem;
    InventoryItemSave tierItem;
    InventoryItemSave storageItem;
    InventoryItemSave logsItem;
    InventoryItemSave filler;

    public InventorySave(String guiName, int size, InventoryItemSave mainItem, InventoryItemSave tierItem, InventoryItemSave storageItem, InventoryItemSave logsItem, InventoryItemSave filler) {
        this.guiName = guiName;
        this.size = size;
        this.mainItem = mainItem;
        this.tierItem = tierItem;
        this.storageItem = storageItem;
        this.logsItem = logsItem;
        this.filler = filler;
    }

}
