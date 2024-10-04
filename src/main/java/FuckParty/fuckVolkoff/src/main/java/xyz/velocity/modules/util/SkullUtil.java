package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.velocity.VelocityFeatures;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static xyz.velocity.modules.masks.CustomMask.skullCache;

public class SkullUtil {

    private static final Object2ObjectOpenHashMap<String, ItemStack> skullCache = new Object2ObjectOpenHashMap<>();

    public static SkullMeta skullMeta(String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", texture));

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

        skullCache.put(texture, head);

        return headMeta;
    }

    public static ItemStack skullMeta(ItemStack head, String texture) {
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", texture));

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

        skullCache.put(texture, head);

        return head;
    }

    public static ItemStack skullItem(String texture) {
        String skinURL = texture;

        if (skullCache.containsKey(skinURL)) return skullCache.get(skinURL);

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (skinURL.isEmpty()) return head;

        SkullMeta headMeta = skullMeta(skinURL);

        head.setItemMeta((ItemMeta) headMeta);

        return head;
    }

}
