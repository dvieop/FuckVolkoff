package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Item
public class PrePearl extends AbstractItem {

    public static Object2ObjectOpenHashMap<Player, Location> toTeleport = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<Player, UUID> thrownPearls = new Object2ObjectOpenHashMap<>();
    private final PartnerItemsConfig config;

    public PrePearl() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "pre_pearl", "&d&lPre Pearl", "ENDER_PEARL", 0, new ArrayList<>(Collections.singleton("Make people not place blocks")), new ArrayList<>(Collections.singleton("")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "pre_pearl";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        if (event instanceof ProjectileHitEvent) {
            projectileHitEvent((ProjectileHitEvent) event);
        }

        if (event instanceof PlayerInteractEvent) {
            interactEvent((PlayerInteractEvent) event);
        }

    }

    private void projectileHitEvent(ProjectileHitEvent e) {

        Player player = (Player) e.getEntity().getShooter();
        String str = player.getUniqueId() + "_" + this.getName();

        if (!PartnerItems.getInstance().canPearlThere(player, e.getEntity().getLocation())) return;

        if (thrownPearls.containsKey(player)) {
            if (thrownPearls.get(player).equals(e.getEntity().getUniqueId())) {

                Location toTP = checkPearlLocation(e.getEntity().getLocation());

                toTeleport.put(player, toTP);
                thrownPearls.remove(player);

                ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

                if (item == null) return;
                if (!item.isEnabled()) return;

                int time = getItemInfo(item);

                BukkitRunnable bukkitRunnable = new BukkitRunnable() {

                    @Override
                    public void run() {
                        toTeleport.remove(player);
                        if (toTP != null) {
                            player.teleport(toTP);
                            //player.teleport(e.getEntity().getLocation());
                        }
                    }

                };

                bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), time * 20);

                PartnerItems.cooldowns.put(str, System.currentTimeMillis() + (item.getCooldown() * 1000L));

                PartnerItems.getInstance().sendMessage(player, config.usedAbilityMsg, item.getDisplayName(), item.getCooldown() + "");
                //PartnerItems.getInstance().updateItem(player);

            }
        }

    }

    private void interactEvent(PlayerInteractEvent e) {

        PartnerItems pI = PartnerItems.getInstance();
        PartnerItemsConfig config = PartnerItemsConfig.getInstance();

        String player = e.getPlayer().getUniqueId() + "_" + this.getName();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;

        if (pI.isOnCooldown(player)) {
            if (System.currentTimeMillis() > pI.getCooldown(player)) {
                PartnerItems.cooldowns.remove(player);
            } else {
                pI.sendMessage(e.getPlayer(), config.onCooldownMsg, item.getDisplayName(), ((pI.getCooldown(player) - System.currentTimeMillis()) / 1000L) + "");

                e.setCancelled(true);
                return;
            }
        }

    }

    private Location checkPearlLocation(Location location) {

        Location l3 = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());

        Location l4 = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ());

        WorldBorder worldBorder = location.getWorld().getWorldBorder();

        double radius = worldBorder.getSize() / 2;

        double xMin = worldBorder.getCenter().getX() - radius - 1;
        double xMax = worldBorder.getCenter().getX() + radius;

        double zMin = worldBorder.getCenter().getZ() - radius - 1;
        double zMax = worldBorder.getCenter().getZ() + radius;

        if (location.getX() > xMax || location.getX() < xMin || location.getZ() > zMax || location.getZ() < zMin) {
            return null;
        }

        boolean passableBlockOne = isPassable(location, location.getBlock(), true);
        boolean passableBlockUp = isPassable(location, l4.getBlock(), true);
        boolean passableBlockDown = isPassable(location, l3.getBlock(), true);

        if (!passableBlockOne || !passableBlockUp) {

            if ((!passableBlockOne || (location.getBlock().isLiquid() && !passableBlockDown)) || (!passableBlockDown && true)) {
                return null;
            }
        }

        if(!passableBlockUp){
            if(location.getBlockY() <= 2.0){
                return null;
            }
            if(passableBlockDown)location.subtract(0, 1, 0);
            location.subtract(0, 1, 0);
        }

        return location;
    }

    private boolean isPassable(Location location, Block block, boolean strict) {
        if(location.getBlockY() <= 0.0 || location.getBlockY() >= 257.0) return false;

        Material material = block.getType();

        if (block.isLiquid()) return true;

        if(block.getType() == Material.STAINED_GLASS_PANE || block.getType() == Material.THIN_GLASS) return true;

        if (material == Material.TRAP_DOOR || material == Material.IRON_TRAPDOOR) {
            Openable trapDoor = (Openable) block.getState();
            return trapDoor.isOpen();
        }

        if (material.equals(Material.REDSTONE_BLOCK) || material.toString().contains("REDSTONE_LAMP") || material.toString().contains("REDSTONE_ORE")) {
            return false;
        }

        if(isGate(block)){
            Gate gate = (Gate) block.getState().getData();

            return gate.isOpen();
        }

        if (material.toString().contains("SIGN")
                || material.toString().contains("BANNER")
                || material.toString().contains("STATIONARY")
                || material.toString().contains("DOOR")
                || material.toString().contains("REDSTONE")
                || material.toString().contains("RAIL")
                || (material.toString().contains("STEP") && !strict)
                || (material.toString().contains("FENCE")) && !strict) {
            return true;
        }

        if (!strict) {
            switch (material) {
                case SKULL:
                case BED:
                case BED_BLOCK:
                case BREWING_STAND:
                case STAINED_GLASS_PANE:
                case THIN_GLASS:
                case CAKE:
                case STONE_SLAB2:
                case STEP:
                case WOOD_STEP:
                case LEAVES:
                case LEAVES_2:
                    return true;
            }
        }

        switch (material) {
            case AIR:
            case FIRE:
            case LAVA:
            case WATER:
            case VINE:
            case CROPS:
            case NETHER_STALK:
            case POTATO:
            case CARROT:
            case LADDER:
            case DEAD_BUSH:
            case WEB:
            case SNOW:
            case TRIPWIRE:
            case LONG_GRASS:
            case DOUBLE_PLANT:
            case TORCH:
            case LEVER:
            case WOOD_BUTTON:
            case STONE_BUTTON:
            case GOLD_PLATE:
            case IRON_PLATE:
            case WOOD_PLATE:
            case STONE_PLATE:
                return true;
        }

        return false;
    }

    private boolean isGate(Block block){
        return block.getType().toString().contains("FENCE_GATE");
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("timeBeforeTeleport", 20);

        info.toString();

        return info;
    }

    private int getItemInfo(ItemSave item) {

        int duration = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            duration = obj.get("timeBeforeTeleport").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return duration;

    }

}
