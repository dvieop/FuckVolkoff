package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static boolean isAirOrNull(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    public static boolean hasNoItemMeta(ItemStack itemStack) {
        return isAirOrNull(itemStack) || !itemStack.hasItemMeta();
    }

    public static ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        return item;
    }

}
