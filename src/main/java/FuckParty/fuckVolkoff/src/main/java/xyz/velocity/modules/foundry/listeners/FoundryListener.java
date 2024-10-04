package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.bukkitutils.listeners.AutoListener;
import xyz.velocity.modules.foundry.config.CreditsConfig;
import xyz.velocity.modules.foundry.config.FoundryConfig;
import xyz.velocity.modules.foundry.FoundryCap;
import xyz.velocity.modules.foundry.util.RewardUtil;
import xyz.velocity.modules.util.InventoryUtil;

import java.util.*;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;

@AutoListener
public class FoundryListener implements Listener {

    public static List<RewardUtil> rewardsList = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void anvilInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        FoundryConfig config = FoundryConfig.getInstance();

        if (!block.getType().equals(Material.ANVIL)) return;
        if (!block.getWorld().getName().equals(getWorld(config.getFoundry().corner1))) return;
        if (!checkDistance(player, xyz.velocity.modules.util.Location.parseToLocation(config.getHologramLocation()))) return;

        Inventory inv = Bukkit.createInventory(null, 27, VelocityFeatures.chat(config.getFoundry().name));
        addItemContents(inv, player);

        player.openInventory(inv);

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {

        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();

        FoundryConfig config = FoundryConfig.getInstance();

        if (!inv.getName().equals(VelocityFeatures.chat(config.getFoundry().name))) return;
        if (!p.getWorld().getName().equals(getWorld(config.getFoundry().corner1))) return;

        RewardUtil reward = rewardsList.stream().filter(obj -> obj.item.equals(e.getCurrentItem())).findFirst().orElse(null);

        if (reward != null) {

            if (!FoundryCap.isActive) {

                if (checkCreditsAndRemove(reward, p)) {
                    new FoundryCap(reward);

                    p.closeInventory();
                }

            } else {
                p.sendMessage(VelocityFeatures.chat(FoundryConfig.getInstance().getFoundryActive()));

                p.closeInventory();
            }

        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent e) {
        FoundryConfig config = FoundryConfig.getInstance();

        if (e.getEntity().getKiller() == null) return;
        if (!e.getEntity().getWorld().getName().equals(getWorld(config.getFoundry().corner1))) return;
        if (!parseMob(e.getEntity().getName()).equals(config.getGainCreditsFrom())) return;

        Player player = e.getEntity().getKiller();
        Map<UUID, Integer> data = CreditsConfig.getInstance().getData();

        if (!data.containsKey(player.getUniqueId())) {
            data.put(player.getUniqueId(), 0);
        }

        int credits = data.get(player.getUniqueId()).intValue() + config.getCreditsPerKill();

        CreditsConfig.getInstance().getData().put(player.getUniqueId(), credits);
        //CreditsConfig.getInstance().saveData();
    }

    private String parseMob(String name) {
        name = name.replaceAll("§[a-z0-9]", "");
        name = name.toLowerCase().split(" ")[0];
        return name;
    }

    private String getWorld(String cords) {
        return cords.split(":")[3];
    }

    private boolean checkDistance(Player p, Location location) {
        int distance = (int) p.getLocation().distance(location);

        if (distance < 6) return true;
        return false;
    }

    private void addItemContents(Inventory inv, Player p) {
        rewardsList.clear();

        FoundryConfig config = FoundryConfig.getInstance();

        for (JsonElement reward : config.getFoundry().rewards) {

            JsonObject obj = new Gson().fromJson(reward, JsonObject.class);

            List<String> lore = Arrays.asList(VelocityFeatures.chat(obj.get("lore").getAsString().replace("<credits>", CreditsConfig.getInstance().getPlayerCredits(p) + "")).split("\\|"));

            String displayItem = obj.get("displayItem").getAsString();
            String itemName = obj.get("itemName").getAsString();
            String command = obj.get("command").getAsString();

            int slot = obj.get("slot").getAsInt();
            int time = obj.get("time").getAsInt();
            int credits = obj.get("credits").getAsInt();

            ItemStack itemStack = new ItemStack(Material.getMaterial(displayItem), 1);
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat(itemName));
            meta.setLore(lore);

            itemStack.setItemMeta(meta);

            inv.setItem(slot, itemStack);

            rewardsList.add(new RewardUtil(itemStack, time, command, credits));

        }

        if (config.fillEmptyUiSlots) {
            InventoryUtil.fillEmptySlots(inv, config.fillMaterial, config.fillMaterialDamage);
        }
    }

    private boolean checkCreditsAndRemove(RewardUtil reward, Player p) {

        CreditsConfig config = CreditsConfig.getInstance();

        if (!config.getData().containsKey(p.getUniqueId())) return false;

        int credits = config.getPlayerCredits(p);

        if (credits >= reward.credits) {

            config.getData().put(p.getUniqueId(), credits - reward.credits);

            //config.saveData();

            return true;

        } else {
            p.sendMessage(VelocityFeatures.chat(FoundryConfig.getInstance().getNotEnoughCredits().replace("<credits>", reward.credits + "")));
            p.closeInventory();

            return false;
        }
    }

    private static FoundryListener instance;

    public FoundryListener() {
        instance = this;
    }

    public static FoundryListener getInstance() {
        return instance;
    }

}
