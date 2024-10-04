package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.customitems.commands.CustomItemsCommand;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.customitems.listeners.ItemsListener;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module
public class PartnerItems extends AbstractModule {

    public static Object2ObjectOpenHashMap<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    @Getter
    private static PartnerItems instance;

    public PartnerItems() {
        new ItemManager();
        instance = this;
    }

    public ItemStack buildItem(String type, int amount) {

        ItemSave itemSave = PartnerItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(type)).findFirst().orElse(null);

        if (itemSave == null) return null;

        ItemStack item = new ItemStack(Material.getMaterial(itemSave.getDisplayItem()), amount, (byte) itemSave.getDamage());

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(itemSave.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", itemSave.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(item);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_customItems_item");

        nbtCompound.setString("id", type);

        return nbtItem.getItem();

    }

    public boolean isOnCooldown(String s) {
        return cooldowns.containsKey(s);
    }

    public long getCooldown(String s) {
        return cooldowns.get(s);
    }

    public ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public void updateItem(Player player) {
        int set = player.getItemInHand().getAmount() - 1;

        if (set == 0) {
            player.getInventory().remove(player.getItemInHand());
            return;
        }

        player.getItemInHand().setAmount(set);
    }

    public void updateItem(Player player, String name) {
        ItemStack item = Arrays.stream(player.getInventory().getContents()).filter(obj -> obj.getItemMeta().getDisplayName().equals(name)).findFirst().orElse(null);

        if (item == null) return;

        int set = item.getAmount() - 1;

        if (set == 0) {
            player.getInventory().remove(item);
            return;
        }

        item.setAmount(set);
    }

    public boolean canPearlThere(Player p, Location loc) {
        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(worldGuardPlugin == null) return false;
        if(!worldGuardPlugin.isEnabled()) return false;

        RegionQuery regionQuery = worldGuardPlugin.getRegionContainer().createQuery();
        regionQuery.testState(loc, p, DefaultFlag.ENDERPEARL);

        if (!regionQuery.testState(loc, p, DefaultFlag.ENDERPEARL)) {
            return false;
        }

        return true;
    }

    public void handleEffects(Player player, List<String> effects, int duration) {

        List<PotionEffect> activePot = new ArrayList<>();

        for (String effect : effects) {
            PotionEffect potion = EnchantUtil.getEffect(effect, duration);

            if (player.hasPotionEffect(potion.getType())) {
                PotionEffect currentPotion = player.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(potion.getType())).findFirst().orElse(null);

                if (currentPotion == null) continue;

                activePot.add(currentPotion);
                player.removePotionEffect(potion.getType());
            }

            player.addPotionEffect(potion);
        }

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (!activePot.isEmpty()) {
                    for (PotionEffect potionEffect : activePot) {
                        player.addPotionEffect(potionEffect);
                    }
                }
            }

        };

        bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), duration + 2);

    }

    public void sendMessage(Player p, String message, String ability, String cooldown) {
        p.sendMessage(VelocityFeatures.chat(
                message
                        .replace("<ability>", ability)
                        .replace("<cooldown>", cooldown)
        ).split("/n"));
    }

    public void sendMessage(Player p, String message, String ability, String cooldown, String player) {
        p.sendMessage(VelocityFeatures.chat(
                message
                        .replace("<player>", player)
                        .replace("<ability>", ability)
                        .replace("<cooldown>", cooldown)
        ).split("/n"));
    }

    public void sendMessage(Player p, String message, String ability, String cooldown, String player, String duration) {
        p.sendMessage(VelocityFeatures.chat(
                message
                        .replace("<player>", player)
                        .replace("<ability>", ability)
                        .replace("<cooldown>", cooldown)
                        .replace("<duration>", duration)
        ).split("/n"));
    }

    @Override
    public String getName() {
        return "custom_items";
    }

    @Override
    public boolean isEnabled() {
        return PartnerItemsConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new ItemsListener());
        CommandAPI.getInstance().enableCommand(new CustomItemsCommand());

        PartnerItemsConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(ItemsListener.getInstance());
        CommandAPI.getInstance().disableCommand(CustomItemsCommand.class);

        PartnerItemsConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
