package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.saves.PetSave;
import xyz.velocity.modules.pets.config.saves.PetStats;
import xyz.velocity.modules.pets.config.saves.PetTierSave;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CustomPet {

    public static final Object2ObjectOpenHashMap<String, ItemStack> skullCache = new Object2ObjectOpenHashMap<>();

    PetSave petSave;
    ItemStack item;

    public CustomPet(PetSave petSave) {
        this.petSave = petSave;
    }

    public String getTexture() {
        return this.petSave.getTexture();
    }

    public ItemStack getItem() {
        if (this.item == null) {
            ItemStack newItem = getSkull(getTexture());;

            NBTItem nbtItem = new NBTItem(newItem);
            NBTCompound compound = nbtItem.addCompound("velocity_pets_item");

            compound.setString("id", this.petSave.getName());
            //compound.setString("level", 1);
            //compound.setString("xp", 1);
            //compound.setString("xpToLevelUp", 1);

            this.item = nbtItem.getItem();
        }

        return this.item.clone();
    }

    public PetSave getPetSave() {
        return petSave;
    }

    private ItemStack getSkull(String texture) {
        String skinURL = texture;

        if (skullCache.containsKey(skinURL)) return skullCache.get(skinURL);

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        if (skinURL.isEmpty()) return head;

        headMeta.setDisplayName(VelocityFeatures.chat(this.petSave.getDisplayName()
                .replace("<level>", "1")
        ));

        PetTierSave petTierSave = PetsConfig.getInstance().petTiers.stream().filter(obj -> obj.getTier() == petSave.getTier()).findFirst().orElse(null);

        PetStats petStats = new PetStats(petSave.getName(), 1, 0, (int) Pets.getInstance().calculateXP(petTierSave, 1), 1.0);
        String statsLore = Pets.getInstance().getStatsLore(this.petSave, petStats);

        String joinLore = VelocityFeatures.chat(String.join("VDIB", this.petSave.getItemLore())
                .replace("<xp>", petStats.getXp() + "")
                .replace("<level>", petStats.getLevel() + "")
                .replace("<xpToLevelUp>", petStats.getXpToLevelUp() + "")
                .replace("<petStats>", statsLore)
        );

        headMeta.setLore(Arrays.asList(joinLore.split("VDIB")));

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", skinURL));

        Field profileField = null;

        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
            profileField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(headMeta);

        skullCache.put(skinURL, head);
        return head;
    }

    private List<String> getLore(List<String> lore) {
        String updated = VelocityFeatures.chat(String.join("VDIB", lore));

        return Arrays.asList(updated.split("VDIB"));
    }

    private ItemStack updateLore(ItemStack item, PetStats petStats) {
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(this.petSave.getDisplayName()
                .replace("<level>", petStats.getLevel() + "")
        ));

        String statsLore = Pets.getInstance().getStatsLore(this.petSave, petStats);

        String joinLore = VelocityFeatures.chat(String.join("VDIB", this.petSave.getItemLore())
                .replace("<xp>", petStats.getXp() + "")
                .replace("<level>", petStats.getLevel() + "")
                .replace("<xpToLevelUp>", petStats.getXpToLevelUp() + "")
                .replace("<petStats>", statsLore)
        );

        itemMeta.setLore(Arrays.asList(joinLore.split("VDIB")));

        item.setItemMeta(itemMeta);

        return item;
    }

}
