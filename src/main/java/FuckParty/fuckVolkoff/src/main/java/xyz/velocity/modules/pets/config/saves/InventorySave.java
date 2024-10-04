package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class InventorySave {

    String guiName;
    int size;
    int equippedPetSlot;
    InventoryItemSave unEquipItem;
    InventoryItemSave takeOutItem;
    InventoryItemSave lockedItem;
    InventoryItemSave toggleItem;
    List<InventoryItemSave> filler;

    public InventorySave(String guiName, int size, int equippedPetSlot, InventoryItemSave unequipSlot, InventoryItemSave takeOutItem, InventoryItemSave lockedItem, InventoryItemSave toggleItem, List<InventoryItemSave> filler) {
        this.guiName = guiName;
        this.size = size;
        this.equippedPetSlot = equippedPetSlot;
        this.takeOutItem = takeOutItem;
        this.unEquipItem = unequipSlot;
        this.lockedItem = lockedItem;
        this.toggleItem = toggleItem;
        this.filler = filler;
    }

}
