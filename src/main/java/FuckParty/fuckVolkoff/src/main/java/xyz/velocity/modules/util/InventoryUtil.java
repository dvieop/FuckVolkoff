package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;

public class InventoryUtil {

    public static void fillEmptySlots(Inventory inventory, String material, int damage) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null) {
                ItemStack itemStack = new ItemStack(Material.getMaterial(material), 1, (byte) damage);
                ItemMeta meta = itemStack.getItemMeta();

                meta.setDisplayName(VelocityFeatures.chat("&7"));
                itemStack.setItemMeta(meta);

                inventory.setItem(i, itemStack);
            }
        }
    }

    public static boolean isInventoryEmpty(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (!ItemUtil.isAirOrNull(item)) {
                return false;
            }
        }

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (!ItemUtil.isAirOrNull(item)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

}
