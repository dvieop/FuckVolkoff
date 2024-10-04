package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.config.saves.ArmorSave;
import xyz.velocity.modules.armorsets.config.saves.SetItemSave;
import xyz.velocity.modules.util.ColorUtil;

import java.util.Arrays;
import java.util.List;

public class CustomSet {
    
    ArmorSave armorSave;
    Object2ObjectOpenHashMap<String, ItemStack> items = new Object2ObjectOpenHashMap<>();

    public CustomSet(ArmorSave armorSave) {
        this.armorSave = armorSave;

        for (SetItemSave item : armorSave.getItems()) {
            loadItem(item);
        }
    }

    public ItemStack getHelmet() {
        return items.get(items.keySet().stream().filter(obj -> obj.endsWith("_HELMET")).findFirst().orElse(null));
    }

    public ItemStack getChestplate() {
        return items.get(items.keySet().stream().filter(obj -> obj.endsWith("_CHESTPLATE")).findFirst().orElse(null));
    }

    public ItemStack getLeggings() {
        return items.get(items.keySet().stream().filter(obj -> obj.endsWith("_LEGGINGS")).findFirst().orElse(null));
    }

    public ItemStack getBoots() {
        return items.get(items.keySet().stream().filter(obj -> obj.endsWith("_BOOTS")).findFirst().orElse(null));
    }

    public Object2ObjectMap<String, ItemStack> getItems() {
        return items;
    }

    private void loadItem(SetItemSave item) {

        ItemStack itemStack = new ItemStack(Material.getMaterial(item.getMaterial()), 1);

        if (itemStack.getType().name().contains("LEATHER")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.spigot().setUnbreakable(true);
            meta.setColor(ColorUtil.getColor(item.getColor()));

            itemStack.setItemMeta(meta);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(item.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", this.armorSave.getArmorLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        itemStack.setItemMeta(itemMeta);

        addEnchantsToItem(itemStack, item.getEnchants());

        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_armorsets_armor");

        nbtCompound.setString("id", this.armorSave.getName());

        String type = itemStack.getType().name();

        items.put(type, nbtItem.getItem());

    }

    private void addEnchantsToItem(ItemStack item, List<String> enchants) {

        for (String enchant : enchants) {

            String[] split = enchant.split(":");

            if (split.length < 2) continue;

            try {
                Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
                item.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }

}
