package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class EnchantTierSave implements Serializable {

    String displayItem;
    int guiSlot;
    String tierName;
    String tierLore;
    int enchantLevel;
    int enchantCost;
    boolean canPurchase;

    public EnchantTierSave(String displayItem, int guiSlot, String tierName, String tierLore, int enchantLevel, int enchantCost, boolean canPurchase) {
        this.displayItem = displayItem;
        this.guiSlot = guiSlot;
        this.tierName = tierName;
        this.tierLore = tierLore;
        this.enchantLevel = enchantLevel;
        this.enchantCost = enchantCost;
        this.canPurchase = canPurchase;
    }

}
