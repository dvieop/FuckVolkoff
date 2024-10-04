package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.Arrays;

@Enchant
public class Shadowstep extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Shadowstep() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Shadowstep", "&cShadowstep", Arrays.asList("&7Teleport behind your opponent on successful hit!"), "WEAPON", "SHADOWSTEP:<level>", 3, 6, 50, 1, false, this.extraInfo());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }

        EnchantManager.nonVanillaEnchants.put(this.getName(), this);
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "Shadowstep";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "SHADOWSTEP";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player violator = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();
        double enchantInformation = getEnchantInformation(violator);

        if (chance < enchantInformation) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violator, violated, this);
            if (procEvent.isCancelled()) return;

            Location newLoc = violated.getLocation().add(getBackHeadDirection(violated, -10).multiply(1.25D));
            newLoc.setY(violated.getLocation().getY());

            if (!isTeleportSafe(newLoc)) return;

            double angle = violator.getLocation().getDirection().angle(violated.getLocation().getDirection()) / 180 * Math.PI;

            if (angle > 0.030) {
                violator.teleport(newLoc);

                procEvent.activationMessage();
            }
        }

    }

    private boolean isTeleportSafe(Location ltc) {
        int height = 4;
        int sideLength = 5;
        int delta = (sideLength / 2);

        Location corner1 = new Location(ltc.getWorld(), ltc.getBlockX() + delta, ltc.getBlockY() + 1, ltc.getBlockZ() - delta);
        Location corner2 = new Location(ltc.getWorld(), ltc.getBlockX() - delta, ltc.getBlockY() + 1, ltc.getBlockZ() + delta);

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for(int x = minX; x <= maxX; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    Block getBlock = ltc.getWorld().getBlockAt(x, ltc.getBlockY() + y, z);

                    if (getBlock.getType() != Material.AIR) return false;
                }
            }
        }

        if (ltc.getWorld().getBlockAt(ltc.getBlockX(), ltc.getBlockY() - 2, ltc.getBlockZ()).getType() == Material.AIR) return false;

        return true;
    }

    private Vector getBackHeadDirection(Player player, double multiply) {
        Vector direction = player.getLocation().getDirection().normalize();
        Vector back = direction.multiply(multiply);

        return back.normalize();
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 5);

        info.toString();

        return info;
    }

    private double getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicDouble chance = new AtomicDouble(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return chance.get();
    }

}
