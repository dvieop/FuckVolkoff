package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Item
public class IronCage extends AbstractItem {

    private final PartnerItemsConfig config;

    public IronCage() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "iron_cage", "&7&lIron Cage", "IRON_FENCE", 0, new ArrayList<>(Collections.singleton("Cage yourself in a 3x3 radius")), new ArrayList<>(Collections.singleton("")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "iron_cage";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        PlayerInteractEvent e = (PlayerInteractEvent) event;

        PartnerItems pI = PartnerItems.getInstance();
        PartnerItemsConfig config = PartnerItemsConfig.getInstance();

        String player = e.getPlayer().getUniqueId() + "_" + this.getName();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;
        if (getDisabledWorlds(item).contains(e.getPlayer().getWorld().getName())) return;

        if (pI.isOnCooldown(player)) {
            if (System.currentTimeMillis() > pI.getCooldown(player)) {
                PartnerItems.cooldowns.remove(player);
            } else {
                pI.sendMessage(e.getPlayer(), config.onCooldownMsg, item.getDisplayName(), ((pI.getCooldown(player) - System.currentTimeMillis()) / 1000L) + "");
                return;
            }
        }

        Player p = e.getPlayer();
        Pair<Integer, Integer> info = getInformation(item);

        buildIronCageAround(p, info.first, 4, info.second);

        PartnerItems.cooldowns.put(player, System.currentTimeMillis() + (item.getCooldown() * 1000L));

        pI.sendMessage(p, config.usedAbilityMsg, item.getDisplayName(), item.getCooldown() + "");
        pI.updateItem(p);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();
        JsonArray array = new JsonArray();

        array.add("Arcade");

        info.addProperty("cageRadius", 3);
        info.addProperty("cageDuration", 4);
        info.add("disabledWorlds", array);

        info.toString();

        return info;
    }

    private Pair<Integer, Integer> getInformation(ItemSave item) {

        int radius = 0;
        int duration = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            radius = obj.get("cageRadius").getAsInt();
            duration = obj.get("cageDuration").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return new Pair<>(radius, duration);

    }

    private List<String> getDisabledWorlds(ItemSave item) {

        List<String> disabledWorlds = new ArrayList<>();

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            JsonArray duration = obj.get("disabledWorlds").getAsJsonArray();

            for (JsonElement jsonElement : duration) {
                disabledWorlds.add(jsonElement.getAsString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return disabledWorlds;

    }

    public void buildIronCageAround(Entity ent, int sideLength, int height, int duration) {
        List<Block> blockList = new ArrayList<>();

        Material fence = Material.IRON_FENCE;
        Material roof = Material.OBSIDIAN;
        Location entLoc = ent.getLocation();

        if(sideLength < 3 || sideLength % 2 == 0) {
            throw new IllegalArgumentException("You must enter an odd number greater than 3 for the side length");
        }else if(height == 0) {
            throw new IllegalArgumentException("Height must be greater than 0.");
        }

        int delta = (sideLength / 2);

        Location corner1 = new Location(entLoc.getWorld(), entLoc.getBlockX() + delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() - delta);
        Location corner2 = new Location(entLoc.getWorld(), entLoc.getBlockX() - delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() + delta);

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for(int x = minX; x <= maxX; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    if((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y, z);
                        if (b.getType() == Material.AIR) {
                            b.setType(fence);
                            blockList.add(b);
                        }
                    }

                    if(y == height - 1) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y + 1, z);
                        if (b.getType() == Material.AIR) {
                            b.setType(roof);
                            blockList.add(b);
                        }
                    }

                    if(y == 0) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y - 1, z);
                        if (b.getType() == Material.AIR) {
                            b.setType(roof);
                            blockList.add(b);
                        }
                    }
                }
            }
        }

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                blockList.forEach(block -> {
                    block.setType(Material.AIR);
                });
            }

        };

        bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), duration * 20);


    }

}
