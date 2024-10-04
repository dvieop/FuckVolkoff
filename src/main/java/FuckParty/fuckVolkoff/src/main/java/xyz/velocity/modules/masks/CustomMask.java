package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.masks.config.MaskSave;
import xyz.velocity.modules.util.SkullUtil;

import java.util.Arrays;
import java.util.List;

public class CustomMask {

    public static final Object2ObjectOpenHashMap<String, ItemStack> skullCache = new Object2ObjectOpenHashMap<>();

    MaskSave maskSave;
    ItemStack item;

    public CustomMask(MaskSave maskSave) {
        this.maskSave = maskSave;
    }

    public String getTexture() {
        return this.maskSave.getTexture();
    }

    public ItemStack getItem() {
        if (this.item == null) {
            ItemStack newItem;

            if (maskSave.getMaterial().equals("SKULL_ITEM")) {
                newItem = getSkull(getTexture());
            } else {
                ItemStack mask = new ItemStack(Material.getMaterial(maskSave.getMaterial()));
                ItemMeta meta = mask.getItemMeta();

                meta.setDisplayName(VelocityFeatures.chat(this.maskSave.getDisplayName()));
                meta.setLore(getLore(this.maskSave.getLore()));

                mask.setItemMeta(meta);

                newItem = mask;
            }

            NBTItem nbtItem = new NBTItem(newItem);
            NBTCompound compound = nbtItem.addCompound("velocity_custommasks_item");

            compound.setString("id", this.maskSave.getName());

            this.item = nbtItem.getItem();
        }

        return this.item.clone();
    }

    public boolean canApply(ItemStack item) {
        if (item.getType().name().endsWith("_HELMET") && !hasMaskApplied(item)) return true;
        return false;
    }

    private boolean hasMaskApplied(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem == null) return false;

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        if (nbtCompound != null) return true;
        return false;
    }

    private ItemStack getSkull(String texture) {
        String skinURL = texture;

        if (skullCache.containsKey(skinURL)) return skullCache.get(skinURL);

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        //SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        if (skinURL.isEmpty()) return head;

        SkullMeta headMeta = SkullUtil.skullMeta(skinURL);

        headMeta.setDisplayName(VelocityFeatures.chat(this.maskSave.getDisplayName()));
        headMeta.setLore(getLore(this.maskSave.getLore()));

        headMeta.spigot().setUnbreakable(true);

        head.setItemMeta((ItemMeta) headMeta);

        skullCache.put(skinURL, head);
        return head;
    }

    private List<String> getLore(List<String> lore) {
        /*if (maskSave.getMultiMask().isEnabled()) {
            String multiLore = maskSave.getMultiMask().getLore();
            lore.add(" ");
            lore.add(multiLore.replace("<mask>", "&7None"));
        }*/

        String updated = VelocityFeatures.chat(String.join("VDIB", lore));

        return Arrays.asList(updated.split("VDIB"));
    }

}
